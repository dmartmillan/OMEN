const filter_aggr = 6
const filter_non_aggr = 0.5

class GoNetic {
    constructor() {
    };

    init(graph, radius, geneSets) {
        // keep a copy of graph argument
        this.graph = graph;
        this.geneSets = geneSets;
        // size settings
        this.radius = radius;
        this.scalingFactor = 0.8;
        this.strokeWidth = 6 * this.scalingFactor * 1.3;
        this.fontSize = 200 * this.scalingFactor;
        this.pieSize = this.scalingFactor;
        this.nodeSize = this.radius * this.scalingFactor;
        this.pieLimit = 24;
        // set highlight mode, neighbors or component
        this.highlightMode = 'neighbors';
        // add buttons
        //this.addHighlightButtons();
        this.addGeneSetButtons();
        // add info
        this.infoElement = document.getElementById('infoText');
        this.defaultInfo = 'Hover over a node to display available information.';
        this.setInfo(this.defaultInfo);
        // create styles
        this.createStyleSheet();
        // determine svg size based on number of nodes
        this.nodeCount = this.graph.nodes.length;
        this.width = 300 * Math.sqrt(this.nodeCount);
        this.height = 200 * Math.sqrt(this.nodeCount);
        // compute node opacity
        /*this.graph.nodes.forEach(n => {
            if (n.mutant.filter(m => m).length === 0) {
                n.opacity = 0.3
            } else {
                n.opacity = 1
            }
        });*/
        // create geneSetMap
        this.geneSetMap = {};
        for (const setID in this.geneSets) {
            this.geneSetMap[setID] = {};
            for (const gene of this.geneSets[setID]) {
                this.geneSetMap[setID][gene] = true;
            }
        }
        // attach geneSet information to nodes
        this.graph.nodes.forEach(n => {
            n.geneSets = {};
            for (const setID in this.geneSets) {
                if (this.geneSetMap[setID][n.id]) {
                    n.geneSets[setID] = true;
                }
            }
        });
        // scale node size if not drawing pies
        this.graph.nodes.forEach(n => {
            let nodeRadius = this.nodeSize;
            if (n.rank_aggr === -1){n.rank_aggr = 100;}
            if (n.rank_non_aggr === -1){n.rank_non_aggr = 100;}
            if(n.rank_aggr < 100 && n.rank_non_aggr < 100){nodeRadius += 10 - ((n.rank_aggr+n.rank_non_aggr)/2)*10*0.01;}
            else if (n.rank_aggr < 100 && n.rank_non_aggr >= 100){nodeRadius += 10 - ((n.rank_aggr+100)/2)*10*0.01;}
            else if (n.rank_aggr >= 100 && n.rank_non_aggr < 100){nodeRadius += 10 - ((100+n.rank_non_aggr)/2)*10*0.01;}
            else {nodeRadius += 10 - (99*10)*0.01;}
            n.radius =  nodeRadius;
        });
        // attach labels to nodes
        const label = {
            nodes: [],
            links: [],
        };
        this.graph.nodes.forEach((d, i) => {
            label.nodes.push({node: d});
            label.nodes.push({node: d});
            label.links.push({
                source: i * 2,
                target: i * 2 + 1
            });
        });
        this.label = label;
        this.labelLayout = d3.forceSimulation(this.label.nodes)
            .force("charge", d3.forceManyBody().strength(-50))
            .force("link", d3.forceLink(label.links).distance(0).strength(2));
        // gather nodes and links from graph data
        this.nodeInDegrees = {};
        this.nodeOutDegrees = {};
        this.neighbors = {};
        this.graph.links.forEach(link => {
            link.weight = -link.max_cost;
	    // increment node degrees
	    this.nodeInDegrees[link.target] = (this.nodeInDegrees[link.target] | 0) + 1;
	    this.nodeOutDegrees[link.source] = (this.nodeOutDegrees[link.source] | 0) + 1;
	    if (this.neighbors[link.source] === undefined) {
	        this.neighbors[link.source] = [];
	    }
	    this.neighbors[link.source].push(link.target);
	    if (this.neighbors[link.target] === undefined) {
	        this.neighbors[link.target] = [];
	    }
	    this.neighbors[link.target].push(link.source);
        });

        function traverse(node, group, neighbors, nodesById) {
            if ("group" in node) {
                node.group = Math.min(node.group, group);
            } else if (neighbors[node.id] !== undefined) {
                node.group = group;
                neighbors[node.id].forEach(id => traverse(nodesById[id], group, neighbors, nodesById));
            }
        }

        this.nodesById = {};
        this.graph.nodes.forEach(node => this.nodesById[node.id] = node);
        //console.log(this.neighbors)
        this.graph.nodes.forEach((node, i) => traverse(node, i, this.neighbors, this.nodesById));
        // determine all the groups and assign them an index
        this.groups = {}
        let groupIdx = 0;
        this.graph.nodes.forEach(node => {
            if (this.groups[node.group] === undefined) {
                this.groups[node.group] = {
                    idx: groupIdx
                }
                groupIdx++;
            }
        })
        // set node group to group index, in this manner the groups are [0, ..., n]
        // set initial node positions, so clusters don't separate eachother
        // note the randomness of the position: if nodes have the same start position, it won't work
        this.graph.nodes.forEach(node => {
            node.group = this.groups[node.group].idx;
            node.x = this.width / groupIdx * (node.group + 0.5 + Math.random());
            node.y = this.height / groupIdx * (node.group + 0.5 + Math.random());
        });
        // create graph layout
        this.graphLayout = d3.forceSimulation(this.graph.nodes)
            .force("charge", d3.forceManyBody().strength(-3000))
            .force("center", d3.forceCenter(this.width / 2, this.height / 2))
            .force("x", d3.forceX(this.width).strength(0.2))
            .force("y", d3.forceY(this.height).strength(0.3))
            .force("cluster", forceCluster())
            .force("collide", forceCollide())
            .force("link", d3.forceLink(this.graph.links)
                .id(d => d.id)
                .distance(50)
                .strength(l => 3 * linkForceStrength(l))
            )
            .on("tick", ticked);
        // create adjacency list
        const adjList = {};
        this.graph.links.forEach(function (d) {
            adjList[d.source.index + "-" + d.target.index] = true;
            adjList[d.target.index + "-" + d.source.index] = true;
        });
        this.adjList = adjList;
        this.svg = d3.select("#svg-container")
            .append("svg")
            // Responsive SVG needs these 2 attributes and no width and height attr.
            .attr("preserveAspectRatio", "xMinYMin slice")
            .attr("viewBox", `0 0 ${this.width} ${this.height}`)
            // Class to make it responsive.
            .classed("svg-content-responsive", true);
        this.appendRule(
            '.svg-container',
            {
                height: '100%',
                width: '100%',
                'min-height': '100%',
            }
        );
        this.appendRule('.svg-content-responsive', {
            '-webkit-box-sizing': 'border-box',
            '-moz-box-sizing': 'border-box',
            'box-sizing': 'border-box',
            height: '100%',
            width: '100%',
            'max-height' : '80vh',
            border: '1px',
            'border-color': 'black',
            'border-style': 'solid',
        });
        this.container = this.svg.append("g");
        this.svg.call(
            d3.zoom()
                .scaleExtent([.1, 10])
                .on("zoom", (event) => this.container.attr("transform", event.transform))
        );
        this.link = this.container.append("g").attr("class", "links")
            .selectAll("path")
            .data(this.graph.links)
            .enter()
            .append("svg:path")
            .attr("class", function (d) {
                return "link " + d.type;
            })
            .attr("marker-end", function (d) {
                return "url(#" + d.type + ")";
            })
            .attr("marker-start", function (d) {
                if (d.direction === "undirected") {
                    return "url(#" + d.type + "undir" + ")";
                }
            })
            .attr("stroke", "#aaa")
            //.attr("stroke-width", d => 5 + ((d.max_cost + 1) * 12) / 8)
            //.attr("opacity", d => (1 + d.max_cost) / 8)
            .attr("opacity", function (d) {
                if (d.type === 'pp_blue' && d.max_cost > filter_non_aggr) {
                    return (1 + d.max_cost) / 8;
                } 
                else if (d.type === 'pp_red' && d.max_cost > filter_aggr) {
                    return (1 + d.max_cost) / 8;
                } 
                else {return 0}
            })
            .attr("stroke-width", function (d) {
                if (d.type === 'pp_blue' && d.max_cost > filter_non_aggr) {
                    return 5 + ((d.max_cost + 1) * 12) / 8;
                } 
                else if (d.type === 'pp_red' && d.max_cost > filter_aggr) {
                    return 5 + ((d.max_cost + 1) * 12) / 8;
                }
                else {return 0}
            })
        this.color = d3.scaleOrdinal(d3.schemeCategory10);
        this.node = this.container.append("g").attr("class", "nodes")
            .selectAll("g")
            .data(this.graph.nodes)
            .enter()
            .append("g")
        this.node.append("circle")
            .attr("r", d => d.radius * .5)
            .attr("fill", "white")
            .attr("endAngle", Math.PI)
        this.node.append("path")
            .attr("d", d3.arc()
                .innerRadius(d => d.radius * .4)
                .outerRadius(d => d.radius * .6)
                .startAngle(0)
                .endAngle(Math.PI * 2))
            .attr("fill", "black")

        /*if (this.isDrawingPies()) {
            this.drawPies();
            if (this.graph.nodes[0].mutant2 !== undefined) {
                this.drawPies(1, 'mutant2');
            }
        }*/

        this.node
            .on("mouseover", focus)
            .on("mouseout", unfocus);
        this.node.call(
            d3.drag()
                .on("start", dragstarted)
                .on("drag", dragged)
                .on("end", dragended)
        );
        this.labelNode = this.container.append("g").attr("class", "labelNodes")
            .selectAll("text")
            .data(this.label.nodes)
            .enter()
            .append("text")
            .text(function (d, i) {
                return i % 2 === 0 ? "" : d.node.id;
            })
            .style("fill", "#000000")
            .style("font-family", "Arial")
            .style("font-size", this.fontSize)
            .style("pointer-events", "none"); // to prevent mouseover/drag capture


        // Per-type markers, as they don't inherit styles.
        this.svg.append("defs").selectAll("marker")
            .data(["pp", "pp_red", "pp_blue", "pd", "sigma", "met", "metabolic", "srna", "phosphorylation", "PDActivator", "PDRepressor", "PDUnknown", "srnaRepression", "srnaActivation", "srnaUnknown"])
            .enter().append("marker")
            .attr("id", function (d) {
                return d;
            })
            .attr("viewBox", "0 -5 10 10")
            .attr("refX", this.radius * 1.5)
            .attr("orient", "auto")
            .attr("markerUnits", "userSpaceOnUse")
            .attr("markerWidth", this.radius * 2)
            .attr("markerHeight", this.radius * 2)
            .append("path")
            .attr("d", "M0,-5L10,0L0,5");

        // These are the markers from undirected interactions
        this.svg.append("defs").selectAll("marker")
            .data(["ppundir", "metundir"])
            .enter().append("marker")
            .attr("id", function (d) {
                return d;
            })
            .attr("viewBox", "-10 -5 10 10")
            .attr("refX", -this.radius * 1.5)
            .attr("refY", 0)
            .attr("markerUnits", "userSpaceOnUse")
            .attr("markerWidth", this.radius * 2)
            .attr("markerHeight", this.radius * 2)
            .attr("orient", "auto")
            .append("path")
            .attr("d", "M0,-5L-10,0L0,5");
    };

    addHighlightButtons() {
        const highlightButtons = document.createElement("div");
        const label = document.createElement("div");
        label.textContent = 'Highlight mode on node hover:';
        highlightButtons.appendChild(label);
        // TODO: this should use different elements, e.g., a dropdown
        ['neighbors', 'component'].forEach(mode => {
            const button = document.createElement("button");
            button.innerHTML = mode;
            button.onclick = () => {this.highlightMode = mode;};
            highlightButtons.appendChild(button);
        });
        document.getElementById('buttons').appendChild(highlightButtons);
    }

    addPieButtons(pieNames) {
        //TODO: check boxes to determine which pies to show
    }

    addGeneSetButtons() {
        const geneSetButtons = document.createElement("div");
        const label = document.createElement("div");
        label.textContent = 'Select gene set to highlight:';
        geneSetButtons.appendChild(label);
        for (const setID in this.geneSets) {
            const button = document.createElement("button");
            button.innerHTML = setID;
            button.onclick = () => {this.highlightGeneSet(setID)};
            geneSetButtons.appendChild(button);
        }
        document.getElementById('buttons').appendChild(geneSetButtons);
    }

    highlightGeneSet(setID) {
        const hasGeneSet = d => d.geneSets[setID] ?? false;
        this.node.style("opacity", function (d) {
            return Math.max(0.1, (hasGeneSet(d) ? d.opacity : 0.1));
        });
        this.labelNode.attr("display", function (d, i) {
            if (i % 2 === 0) {
                return 'none';
            }
            //const node = gonetic.graph.nodes[Math.floor(i / 2)]
            return hasGeneSet(d.node) ? "block" : "none";
        });
        this.link.style("opacity", function (d) {
            if(d.max_cost>3){
                const opacity = (1 + d.max_cost) / 8;
            }
            else {const opacity = 0;}
            return hasGeneSet(d.source) && hasGeneSet(d.target)
                ? opacity
                : 0.1;
        });
        this.setInfo(`Highlighting gene set ${setID}`);
    }

    setInfo(idText, information='', productInformation='', geneSetInformation='') {
        this.infoElement.children[0].textContent = idText;
        this.infoElement.children[1].textContent = information;
    }

    appendRule = (selector = '*', style = {}) => {
        const sheet = this.styleSheet;
        const len = sheet.cssRules.length;
        sheet.insertRule('*{}', len);
        const rule = sheet.cssRules[len];
        rule.selectorText = selector;
        Object.getOwnPropertyNames(style).forEach(prop => {
            rule.style[prop] = style[prop];
        });
        return rule;
    }

    createStyleSheet() {
        // create style sheet
        document.body.appendChild(document.createElement('style'));
        this.styleSheet = document.styleSheets[document.styleSheets.length - 1];

        // style rules for nodes
        this.appendRule('.node', {
            fill: '#000000',
            'fill-opacity': '0.8',
        });
        // style rules for links
        this.appendRule('.link', {
            fill: 'none',
            'stroke-opacity': '0.7',
            'stroke-width': '' + this.strokeWidth + 'px',
        });
        // style rules for arcs TODO
        this.appendRule('.arc', {
            fill: 'white',
        });
        // link styles
        // TODO: automate this based on presence in the data
        [
            // pp
            {names: ['pp'], color: '#26a4d4', opacity: 1},
            {names: ['pp_red'], color: 'red', opacity: 0.9},
            {names: ['pp_blue'], color: 'blue', opacity: 0.9},
            // pd
            {names: ['pd', 'PDRepressor'], color: 'red', opacity: 0.9},
            {names: ['PDActivator'], color: 'blue', opacity: 0.9},
            {names: ['PDUnknown'], color: 'LightGray', opacity: 0.9},
            // (de)phosphorylation
            {names: ['phosphorylation'], color: 'purple', opacity: 0.9},
            {names: ['dephosphorylation'], color: 'brown', opacity: 0.9},
            // met
            {names: ['met', 'metabolic'], color: 'orange', opacity: 0.9},
            // srna
            {names: ['srna'], color: 'black', opacity: 0.9},
            // sigma
            {names: ['sigma'], color: 'steelblue', opacity: 0.9},
        ].forEach(linkStyle => {
            const color = linkStyle.color;
            const opacity = linkStyle.opacity;
            linkStyle.names.forEach(name => {
                // link style based on class
                this.appendRule(`.link.${name}`, {
                    stroke: color,
                });
                // marker styles based on id TODO
                [name, `${name}undir`].forEach(id => {
                    this.appendRule(`#${id}`, {
                        fill: color,
                        opacity: opacity,
                    });
                });
            });
        });
    }

    /*getPiePieces(prop = 'mutant') {
        return Math.max(...this.graph.nodes.map(x => x[prop].length));
    }*/

    /*isDrawingPies(prop = 'mutant') {
        // determine if we should draw pies
        return this.getPiePieces(prop) <= this.pieLimit;
    }*/

    /*drawPies(round = 0, prop = 'mutant') {
        if (prop !== 'mutant') {
            this.node.append("path")
                .attr("d", d3.arc()
                    .innerRadius(d => d.radius * (.6 + .9 * round - .2))
                    .outerRadius(d => d.radius * (.6 + .9 * round))
                    .startAngle(0)
                    .endAngle(Math.PI * 2))
                .attr("fill", "white")
        }
        const pieces = this.getPiePieces(prop);
        const arc = (piece) => d3.arc()
            .innerRadius(d => d.radius * (.6 + .9 * round))
            .outerRadius(d => d.radius * (.6 + .9 * (round + 1)))
            .startAngle(piece * Math.PI * 2 / pieces)
            .endAngle((piece + 1) * Math.PI * 2 / pieces)
        for (let i = 0; i < pieces; i++) {
            this.node.append("path")
                .attr("d", arc(i))
                .attr("fill", this.color(i))
                .attr("opacity", d => d[prop][i] ? 1 : 0)
        }
    }*/

    unfreeze() {
        this.graph.nodes.forEach(d => {
            d.fx = null;
            d.fy = null;
        });
    }
}

window.gonetic = new GoNetic();
const gonetic = window.gonetic;

function centroid(nodes) {
    let x = 0;
    let y = 0;
    let z = 0;
    for (const d of nodes) {
        let k = gonetic.radius ** 2;
        x += d.x * k;
        y += d.y * k;
        z += k;
    }
    return {x: x / z, y: y / z};
}

function forceCluster() {
    const strength = 0.07;
    let nodes;

    function force(alpha) {
        const centroids = d3.rollup(nodes, centroid, d => d.group);
        const l = alpha * strength;
        for (const d of nodes) {
            const {x: cx, y: cy} = centroids.get(d.group);
            d.vx -= (d.x - cx) * l;
            d.vy -= (d.y - cy) * l;
        }
    }

    force.initialize = _ => nodes = _;
    return force;
}

function forceCollide() {
    const alpha = 0.5; // fixed for greater rigidity!
    const padding1 = gonetic.radius * 30; // separation between same-color nodes
    const padding2 = gonetic.radius * 30; // separation between different-color nodes
    let nodes;
    let maxRadius;

    function force() {
        const quadtree = d3.quadtree(nodes, d => d.x, d => d.y);
        for (const d of nodes) {
            const r = gonetic.radius + maxRadius;
            const nx1 = d.x - r, ny1 = d.y - r;
            const nx2 = d.x + r, ny2 = d.y + r;
            quadtree.visit((q, x1, y1, x2, y2) => {
                if (!q.length) do {
                    if (q.data !== d) {
                        const r = 2 * gonetic.radius + (d.group === q.data.group ? padding1 : padding2);
                        let x = d.x - q.data.x, y = d.y - q.data.y, l = Math.hypot(x, y);
                        if (l < r) {
                            l = (l - r) / l * alpha;
                            d.x -= x *= l, d.y -= y *= l;
                            q.data.x += x, q.data.y += y;
                        }
                    }
                } while (q = q.next);
                return x1 > nx2 || x2 < nx1 || y1 > ny2 || y2 < ny1;
            });
        }
    }

    force.initialize = _ => {
        nodes = _;
        maxRadius = 20 + Math.max(padding1, padding2);
    }
    return force;
}

const linkForceStrength = link => {
    if (gonetic.nodeOutDegrees[link.target.id] === 1) {
        return 0.9;
    }
    if (gonetic.nodeOutDegrees[link.source.id] > 5 && gonetic.nodeOutDegrees[link.target.id] < 3) {
        return 0.7;
    } else if (gonetic.nodeOutDegrees[link.source.id] < 4 || gonetic.nodeOutDegrees[link.target.id] < 4) {
        return 0.3;
    } else if (gonetic.nodeOutDegrees[link.source.id] > 10 || gonetic.nodeOutDegrees[link.target.id] > 10) {
        return 0.3;
    }
    return 0.4;
}
const neigh = (a, b) => {
    return a === b || gonetic.adjList[a + "-" + b];
};

const ticked = (event) => {
    gonetic.node.call(updateNode);
    gonetic.link.call(updateLink);
    gonetic.labelLayout.alphaTarget(0.3).restart();
    gonetic.labelNode.each(function (d, i) {
        if (i % 2 === 0) {
            d.x = d.node.x;
            d.y = d.node.y;
        } else {
            const b = this.getBBox();
            const diffX = d.x - d.node.x;
            const diffY = d.y - d.node.y;
            const dist = Math.sqrt(diffX * diffX + diffY * diffY);
            let shiftX = b.width * (diffX - dist) / (dist * 2);
            shiftX = Math.max(-b.width, Math.min(0, shiftX));
            const shiftY = 16;
            this.setAttribute("transform", "translate(" + shiftX + "," + shiftY + ")");
        }
    });
    gonetic.labelNode.call(updateNode);
}
const fixna = (x) => {
    return isFinite(x) ? x : 0;
}
const updateLink = (link) => {
    link.attr("d", linkArc)
        .attr("marker-end", d => "url(#" + d.type + ")")
        .attr("marker-start", function (d) {
            if (d.direction === "undirected") {
                return "url(#" + d.type + "undir" + ")";
            }
        });
}
// Add different arc bendings for each data type so paths never overlap
const linkArc = d => {
    const dx = d.target.x - d.source.x;
    const dy = d.target.y - d.source.y;
    const distance = Math.sqrt(dx * dx + dy * dy);
    let multiplier = 1;
    switch (d.type) {
        case 'PDRepressor':
        case 'PDActivator':
        case 'PDUnknown':
        case 'pp':
            multiplier = 3;
            break;
        case 'pp_red':
            multiplier = 3;
            break;
        case 'pp_blue':
            multiplier = 3;
            break;
        case 'met':
        case 'metabolic':
            multiplier = 1.75;
            break;
        case 'pd':
            multiplier = 1.25;
            break;
        case 'sigma':
            multiplier = 1;
            break;
        case 'srna':
            multiplier = 0.75;
            break;
        case 'phosphorylation':
            multiplier = 0.85;
            break;
        case 'dephosphorylation':
            multiplier = 0.675;
            break;
    }
    // halve radius multiplier for clearer bends
    multiplier /= 2;
    // determine radius
    const dr = multiplier * distance;
    return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
}

let focus = (event, d) => {
    const datum = d3.select(event.target).datum();
    let compare = (_) => true;
    switch (gonetic.highlightMode) {
        case 'neighbors':
            compare = d => neigh(datum.index, d.index);
            break;
        case 'component':
            compare = d => datum.group === d.group;
            break;
    }
    gonetic.node.style("opacity", function (d) {
        return Math.max(0.1, (compare(d) ? d.opacity : 0.1));
    });
    gonetic.labelNode.attr("display", function (d, i) {
        if (i % 2 === 0) {
            return 'none';
        }
        //const node = gonetic.graph.nodes[Math.floor(i / 2)]
        return compare(d.node) ? "block" : "none";
    });
    gonetic.link.style("opacity", function (d) {
        if(d.type === 'pp_blue' && d.max_cost > filter_non_aggr){
                const opacity = (1 + d.max_cost) / 8;
            }
        else if(d.type === 'pp_red' && d.max_cost > filter_aggr){
                const opacity = (1 + d.max_cost) / 8;
            }
        else {const opacity = 0;}
        let opacity;
        return compare(d.source) && compare(d.target)
            ? opacity
            : 0.1;
    });
    //let mutants = d.mutant.map((x, i) => x ? window.graph.conditions[i] : undefined).filter(i => i !== undefined);
    let nodeOfInterestText = `${d.id} has rank_aggr ${d.rank_aggr} and rank_non_aggr ${d.rank_non_aggr}.`;
    /*if (mutants.length > 0) {
        nodeOfInterestText = `${d.id} is mutated in: ${mutants.join(', ')}.`;
    }*/
    gonetic.setInfo(
        `Selected node: ${d.id}`,
        nodeOfInterestText,
    );
}
const unfocus = (event) => {
    gonetic.labelNode.attr("display", "block");
    gonetic.node.style("opacity", d => d.opacity);
    //gonetic.link.style("opacity", d => (1 + d.max_cost) / 8);
    gonetic.link.style("opacity", function (d) {
                if (d.type === 'pp_blue' && d.max_cost > filter_non_aggr) {
                    return (1 + d.max_cost) / 8;
                } 
                else if (d.type === 'pp_red' && d.max_cost > filter_aggr) {
                    return (1 + d.max_cost) / 8;
                } 
                else {return 0}
            })
}
const updateNode = (node) => {
    node.attr("transform", function (d) {
        return "translate(" + fixna(d.x) + "," + fixna(d.y) + ")";
    });
}
const dragstarted = (event, d) => {
    event.sourceEvent.stopPropagation();
    if (!event.active) {
        gonetic.graphLayout.alphaTarget(0.3).restart();
    }
    d.fx = d.x;
    d.fy = d.y;
}
const dragged = (event, d) => {
    d.fx = event.x;
    d.fy = event.y;
}
const dragended = (event, d) => {
    if (!event.active) {
        gonetic.graphLayout.alphaTarget(0);
    }
}
init = () => {
    if (!window.graph || !window.d3) {
        setTimeout(init, 100);
    }
    window.gonetic.init(window.graph, 7.5, window.geneSets);
}
setTimeout(init, 100);

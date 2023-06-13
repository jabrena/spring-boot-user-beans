const radius = 36;//36

var force = -5; //-1200; //2400
var decay_force = force;

var resource = "/graph1"; //"graph.json";

var symbol = d3.symbol().size([radius * 100])

var svg = d3.select("svg"),
  width = +svg.attr("width"),
  height = +svg.attr("height");
  svg.call(d3.zoom()
    .scaleExtent([0.1, 8])
    .on("zoom", function () {
      g.attr("transform", d3.event.transform)
      // node.attr("fill", "black");  // force redraw
    }))
    .on("dblclick.zoom", null);
svg.style("cursor","move");

//add encompassing group for the zoom
var g = svg.append("g")
  .attr("class", "everything");

var color = d3.scaleOrdinal(d3.schemeCategory20);
var active_node = null;
var focus_node = null;

const vw = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);
const vh = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);

var simulation = d3.forceSimulation()
  .force("link", d3.forceLink().id(function (d) { return d.id; }).distance(50))
  .force("charge", d3.forceManyBody().strength(force))
  .force("collide", d3.forceCollide(radius + 10).iterations(10))
  .force("center", d3.forceCenter(vw / 2, vh / 2))
  .alphaDecay(0.01)
  .velocityDecay(0.1);

d3.json(resource, function (error, graph) {
  if (error) throw error;

  var link_set = {};
  var in_degree_dict = {};
  graph.links.forEach(function(d) {
    link_set[encode_link(d.source, d.target)] = true;
    if (!in_degree_dict[d.target])
      in_degree_dict[d.target] = 0;
    in_degree_dict[d.target]++;
  });

  // For arrowhead
  g.append("svg:defs").selectAll("marker")
    .data(["arrow"])
    .enter().append("svg:marker")
    .attr("id", String)
    .attr("viewBox", "0 -5 11.3 10") // shift arrow
    .attr("refX", 60)
    .attr("refY", 0)
    .attr("markerWidth", 8)
    .attr("markerHeight", 8)
    .attr("orient", "auto")
    .append("svg:path")
    .attr("d", "M0,-5L10,0L0,5");

  var link = g.append("g")
    .attr("class", "links")
    .selectAll("line")
    .data(graph.links.filter(function(d) {
        if (d.comment) return null;
        if (d.value[0] !== "#")
          return this;
        return null;
      }))
    .enter().append("line")
    /*.attr("stroke-width", function (d) { return Math.sqrt(d.value); })*/
    .attr("stroke-width", "1px")
  // .style("stroke", function(d) { return color(d.group); });

  var node = g.append("g")
    .attr("class", "nodes")
    .selectAll("g")
    .data(graph.nodes
      .filter(function(d) {
        if (d.comment) return null;
        color(d.group);
        if (d.id[0] !== "#")
          return this;
        return null;
      }))
    .enter().append("g")
    .call(d3.drag()
      .on("start", dragstarted)
      .on("drag", dragged)
      .on("end", dragended));

  var circles = node.append("path")
    .style("fill", function (d) { return color(d.group); })
    .style("stroke", function(d) { return color(d.group) })
    .attr("d", symbol.type(function (d) {
      if (in_degree_dict[d.id]) {
        return d3.symbolCircle;
      }
      return d3.symbolSquare;
    }))

  var labels = node.append("text")
    .html(function (d) {
      // Implement line break
      const pad = 1.2;
      const top_pad = (d.id.match(/\n/g) || []).length * -pad / 2;
      const head = "<tspan x='0' dy='" + top_pad + "em'>";
      const begin = "<tspan x='0' dy='" + pad + "em'>";
      const end = "</tspan>";
      text = head + d.id + end;
      text = text.replace(/\n/g, end + begin);
      return text;
    })

  function update_force() {
    const bound = -100;//200
    decay_force += 100;//200
    if (Math.abs(decay_force) < Math.abs(bound)) {
      decay_force = bound;
      simulation.force("charge", d3.forceManyBody().strength(decay_force))
      return;
    }
    simulation.force("charge", d3.forceManyBody().strength(decay_force))
    setTimeout(update_force, 200);
  }

  setTimeout(update_force, 200);

  function update_screen(d) {
    if (active_node) {
      node.attr("fill", function(d) {
        return is_connected(active_node, d.id) ? "yellow" : "black";
      });
      node.attr("opacity", function(d) {
        return focus_node && !is_connected(active_node, d.id) ? "10%" : "100%";
      });
      circles.style("stroke", function(d) {
        if (link_set[encode_link(active_node, d.id)]) {
          return "blue";
        } else if (link_set[encode_link(d.id, active_node)]) {
          return "red";
        } else if (active_node === d.id) {
          return "black";
        }
        return color(d.group);
      });
      link.style("stroke", function(d) {
        if (active_node === d.source.id) {
          return "blue";
        } else if (active_node === d.target.id) {
          return "red";
        }
        return "#999";
      });
      link.attr("opacity", function(d) {
        return focus_node && active_node !== d.source.id && active_node !== d.target.id ? "0%" : "100%";
      });
    } else {
      node.attr("fill", "black");
      node.attr("opacity", "100%");
      circles.style("stroke", function(d) { return color(d.group) });
      link.style("stroke", "#999");
      link.attr("opacity", "100%");
    }
  }

  node.on("mouseover", function(d, i) {
    if (focus_node)
      return;
    active_node = d.id;
    update_screen(d);
  }).on("mouseout", function(d, i) {
    if (focus_node)
      return;
    active_node = null;
    update_screen(d);
  }).on("click", function(d, i) {
    console.log(d.id, "clicked");
  });

  function encode_link(a, b) {
    return a + "->" + b;
  }

  function is_connected(a, b) {
    return link_set[encode_link(a, b)] || link_set[encode_link(b, a)] || a == b;
  }

  function is_neighbor(v) {
    var a = v.index;
    for (var pair in link_set) {
      s = pair.split("->");
      if ((s[0] == a || s[1] == a)) {
        return true;
      }
    }
    return false;
  }

  function shade_color(color, percent) {
    var R = parseInt(color.substring(1,3),16);
    var G = parseInt(color.substring(3,5),16);
    var B = parseInt(color.substring(5,7),16);

    R = parseInt(R * (100 + percent) / 100);
    G = parseInt(G * (100 + percent) / 100);
    B = parseInt(B * (100 + percent) / 100);

    R = (R<255)?R:255;
    G = (G<255)?G:255;
    B = (B<255)?B:255;

    var RR = ((R.toString(16).length==1)?"0"+R.toString(16):R.toString(16));
    var GG = ((G.toString(16).length==1)?"0"+G.toString(16):G.toString(16));
    var BB = ((B.toString(16).length==1)?"0"+B.toString(16):B.toString(16));

    return "#"+RR+GG+BB;
  }

  node.append("title")
    .text(function (d) { return d.id; });

  simulation
    .nodes(graph.nodes)
    .on("tick", ticked)

  simulation.force("link")
    .links(graph.links);

  function ticked() {
    link
      .attr("x1", function (d) { return d.source.x; })
      .attr("y1", function (d) { return d.source.y; })
      .attr("x2", function (d) { return d.target.x; })
      .attr("y2", function (d) { return d.target.y; });

    node
      .attr("transform", function (d) {
        return "translate(" + d.x + "," + d.y + ")";
      })
  }

  function dragstarted(d) {
    if (!d3.event.active) simulation.alphaTarget(0.3).restart();
    d.fx = d.x;
    d.fy = d.y;
    focus_node = active_node;
    update_screen(d);
  }

  function dragged(d) {
    d.fx = d3.event.x;
    d.fy = d3.event.y;
  }

  function dragended(d) {
    if (!d3.event.active) simulation.alphaTarget(0);
    d.fx = null;
    d.fy = null;
    focus_node = null;
    // TODO: If drag end, while mouse out (due to repulse force), the active_node won't be reset.
    update_screen(d);
  }
});

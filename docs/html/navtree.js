var NAVTREE =
[
  [ "Hybris", "index.html", [
    [ "Class List", "annotated.html", [
      [ "com.kumquat.hybris.databases.HybrisDatabaseHelper", "classcom_1_1kumquat_1_1hybris_1_1databases_1_1_hybris_database_helper.html", null ],
      [ "com.kumquat.hybris.Ingredient", "classcom_1_1kumquat_1_1hybris_1_1_ingredient.html", null ],
      [ "com.kumquat.hybris.Inventory", "classcom_1_1kumquat_1_1hybris_1_1_inventory.html", null ],
      [ "com.kumquat.hybris.InventoryActivity", "classcom_1_1kumquat_1_1hybris_1_1_inventory_activity.html", null ],
      [ "com.kumquat.hybris.databases.InventoryDatabaseHelper", "classcom_1_1kumquat_1_1hybris_1_1databases_1_1_inventory_database_helper.html", null ],
      [ "com.kumquat.hybris.Item", "classcom_1_1kumquat_1_1hybris_1_1_item.html", null ],
      [ "com.kumquat.hybris.MainPageActivity", "classcom_1_1kumquat_1_1hybris_1_1_main_page_activity.html", null ],
      [ "com.kumquat.hybris.ManualAddActivity", "classcom_1_1kumquat_1_1hybris_1_1_manual_add_activity.html", null ],
      [ "com.kumquat.hybris.Recipe", "classcom_1_1kumquat_1_1hybris_1_1_recipe.html", null ],
      [ "com.kumquat.hybris.RecipeActivity", "classcom_1_1kumquat_1_1hybris_1_1_recipe_activity.html", null ],
      [ "com.kumquat.hybris.RecipeListActivity", "classcom_1_1kumquat_1_1hybris_1_1_recipe_list_activity.html", null ],
      [ "com.kumquat.hybris.databases.RecipeYAMLParser", "classcom_1_1kumquat_1_1hybris_1_1databases_1_1_recipe_y_a_m_l_parser.html", null ],
      [ "com.kumquat.hybris.SplashscreenActivity", "classcom_1_1kumquat_1_1hybris_1_1_splashscreen_activity.html", null ],
      [ "com.kumquat.hybris.Test", "classcom_1_1kumquat_1_1hybris_1_1_test.html", null ]
    ] ],
    [ "Class Index", "classes.html", null ],
    [ "Class Members", "functions.html", null ],
    [ "Packages", "namespaces.html", [
      [ "com", "namespacecom.html", null ],
      [ "com.kumquat", "namespacecom_1_1kumquat.html", null ],
      [ "com.kumquat.hybris", "namespacecom_1_1kumquat_1_1hybris.html", null ],
      [ "com.kumquat.hybris.databases", "namespacecom_1_1kumquat_1_1hybris_1_1databases.html", null ]
    ] ],
    [ "File List", "files.html", [
      [ "src/com/kumquat/hybris/Ingredient.java", "_ingredient_8java.html", null ],
      [ "src/com/kumquat/hybris/Inventory.java", "_inventory_8java.html", null ],
      [ "src/com/kumquat/hybris/InventoryActivity.java", "_inventory_activity_8java.html", null ],
      [ "src/com/kumquat/hybris/Item.java", "_item_8java.html", null ],
      [ "src/com/kumquat/hybris/MainPageActivity.java", "_main_page_activity_8java.html", null ],
      [ "src/com/kumquat/hybris/ManualAddActivity.java", "_manual_add_activity_8java.html", null ],
      [ "src/com/kumquat/hybris/Recipe.java", "_recipe_8java.html", null ],
      [ "src/com/kumquat/hybris/RecipeActivity.java", "_recipe_activity_8java.html", null ],
      [ "src/com/kumquat/hybris/RecipeListActivity.java", "_recipe_list_activity_8java.html", null ],
      [ "src/com/kumquat/hybris/SplashscreenActivity.java", "_splashscreen_activity_8java.html", null ],
      [ "src/com/kumquat/hybris/Test.java", "_test_8java.html", null ],
      [ "src/com/kumquat/hybris/databases/HybrisDatabaseHelper.java", "_hybris_database_helper_8java.html", null ],
      [ "src/com/kumquat/hybris/databases/InventoryDatabaseHelper.java", "_inventory_database_helper_8java.html", null ],
      [ "src/com/kumquat/hybris/databases/RecipeYAMLParser.java", "_recipe_y_a_m_l_parser_8java.html", null ]
    ] ]
  ] ]
];

function createIndent(o,domNode,node,level)
{
  if (node.parentNode && node.parentNode.parentNode)
  {
    createIndent(o,domNode,node.parentNode,level+1);
  }
  var imgNode = document.createElement("img");
  if (level==0 && node.childrenData)
  {
    node.plus_img = imgNode;
    node.expandToggle = document.createElement("a");
    node.expandToggle.href = "javascript:void(0)";
    node.expandToggle.onclick = function() 
    {
      if (node.expanded) 
      {
        $(node.getChildrenUL()).slideUp("fast");
        if (node.isLast)
        {
          node.plus_img.src = node.relpath+"ftv2plastnode.png";
        }
        else
        {
          node.plus_img.src = node.relpath+"ftv2pnode.png";
        }
        node.expanded = false;
      } 
      else 
      {
        expandNode(o, node, false);
      }
    }
    node.expandToggle.appendChild(imgNode);
    domNode.appendChild(node.expandToggle);
  }
  else
  {
    domNode.appendChild(imgNode);
  }
  if (level==0)
  {
    if (node.isLast)
    {
      if (node.childrenData)
      {
        imgNode.src = node.relpath+"ftv2plastnode.png";
      }
      else
      {
        imgNode.src = node.relpath+"ftv2lastnode.png";
        domNode.appendChild(imgNode);
      }
    }
    else
    {
      if (node.childrenData)
      {
        imgNode.src = node.relpath+"ftv2pnode.png";
      }
      else
      {
        imgNode.src = node.relpath+"ftv2node.png";
        domNode.appendChild(imgNode);
      }
    }
  }
  else
  {
    if (node.isLast)
    {
      imgNode.src = node.relpath+"ftv2blank.png";
    }
    else
    {
      imgNode.src = node.relpath+"ftv2vertline.png";
    }
  }
  imgNode.border = "0";
}

function newNode(o, po, text, link, childrenData, lastNode)
{
  var node = new Object();
  node.children = Array();
  node.childrenData = childrenData;
  node.depth = po.depth + 1;
  node.relpath = po.relpath;
  node.isLast = lastNode;

  node.li = document.createElement("li");
  po.getChildrenUL().appendChild(node.li);
  node.parentNode = po;

  node.itemDiv = document.createElement("div");
  node.itemDiv.className = "item";

  node.labelSpan = document.createElement("span");
  node.labelSpan.className = "label";

  createIndent(o,node.itemDiv,node,0);
  node.itemDiv.appendChild(node.labelSpan);
  node.li.appendChild(node.itemDiv);

  var a = document.createElement("a");
  node.labelSpan.appendChild(a);
  node.label = document.createTextNode(text);
  a.appendChild(node.label);
  if (link) 
  {
    a.href = node.relpath+link;
  } 
  else 
  {
    if (childrenData != null) 
    {
      a.className = "nolink";
      a.href = "javascript:void(0)";
      a.onclick = node.expandToggle.onclick;
      node.expanded = false;
    }
  }

  node.childrenUL = null;
  node.getChildrenUL = function() 
  {
    if (!node.childrenUL) 
    {
      node.childrenUL = document.createElement("ul");
      node.childrenUL.className = "children_ul";
      node.childrenUL.style.display = "none";
      node.li.appendChild(node.childrenUL);
    }
    return node.childrenUL;
  };

  return node;
}

function showRoot()
{
  var headerHeight = $("#top").height();
  var footerHeight = $("#nav-path").height();
  var windowHeight = $(window).height() - headerHeight - footerHeight;
  navtree.scrollTo('#selected',0,{offset:-windowHeight/2});
}

function expandNode(o, node, imm)
{
  if (node.childrenData && !node.expanded) 
  {
    if (!node.childrenVisited) 
    {
      getNode(o, node);
    }
    if (imm)
    {
      $(node.getChildrenUL()).show();
    } 
    else 
    {
      $(node.getChildrenUL()).slideDown("fast",showRoot);
    }
    if (node.isLast)
    {
      node.plus_img.src = node.relpath+"ftv2mlastnode.png";
    }
    else
    {
      node.plus_img.src = node.relpath+"ftv2mnode.png";
    }
    node.expanded = true;
  }
}

function getNode(o, po)
{
  po.childrenVisited = true;
  var l = po.childrenData.length-1;
  for (var i in po.childrenData) 
  {
    var nodeData = po.childrenData[i];
    po.children[i] = newNode(o, po, nodeData[0], nodeData[1], nodeData[2],
        i==l);
  }
}

function findNavTreePage(url, data)
{
  var nodes = data;
  var result = null;
  for (var i in nodes) 
  {
    var d = nodes[i];
    if (d[1] == url) 
    {
      return new Array(i);
    }
    else if (d[2] != null) // array of children
    {
      result = findNavTreePage(url, d[2]);
      if (result != null) 
      {
        return (new Array(i).concat(result));
      }
    }
  }
  return null;
}

function initNavTree(toroot,relpath)
{
  var o = new Object();
  o.toroot = toroot;
  o.node = new Object();
  o.node.li = document.getElementById("nav-tree-contents");
  o.node.childrenData = NAVTREE;
  o.node.children = new Array();
  o.node.childrenUL = document.createElement("ul");
  o.node.getChildrenUL = function() { return o.node.childrenUL; };
  o.node.li.appendChild(o.node.childrenUL);
  o.node.depth = 0;
  o.node.relpath = relpath;

  getNode(o, o.node);

  o.breadcrumbs = findNavTreePage(toroot, NAVTREE);
  if (o.breadcrumbs == null)
  {
    o.breadcrumbs = findNavTreePage("index.html",NAVTREE);
  }
  if (o.breadcrumbs != null && o.breadcrumbs.length>0)
  {
    var p = o.node;
    for (var i in o.breadcrumbs) 
    {
      var j = o.breadcrumbs[i];
      p = p.children[j];
      expandNode(o,p,true);
    }
    p.itemDiv.className = p.itemDiv.className + " selected";
    p.itemDiv.id = "selected";
    $(window).load(showRoot);
  }
}


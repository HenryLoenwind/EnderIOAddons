package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.extended.NamedMapConverter;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;

public class TestX {

  public TestX() {
		super();
	}

private String configPath = ".";

  public static void main(String[] args) throws IOException {
    (new TestX()).run();
  }

  private void run() throws IOException {
    XStream xstream = makeXStream();
    
    Config cfg = new Config((Water) readConfig(xstream, "test.xml"));

    dump(xstream, cfg);

    // go(xstream);
  }

  public XStream makeXStream() {
    XStream xstream = new XStream();
    if (this.getClass().getClassLoader() != null) {
      xstream.setClassLoader(this.getClass().getClassLoader());
    }
    
//    xstream.setMode(XStream.ID_REFERENCES);
    // xstream.aliasPackage("", "test");
    xstream.registerLocalConverter(Water.class, "contents",
    new NamedMapConverter(xstream.getMapper(), "component", "name", String.class, "ppm", Double.class, true, true, xstream.getConverterLookup())
    );
    xstream.addImplicitCollection(Material.class, "components");
    xstream.useAttributeFor(Material.class, "name");
    xstream.useAttributeFor(Material.class, "prio");
    xstream.useAttributeFor(Material.class, "volume");
    xstream.useAttributeFor(Material.class, "density");
    xstream.useAttributeFor(Component.class, "name");
    xstream.useAttributeFor(Component.class, "factor");
    xstream.useAttributeFor(Component.class, "count");
    xstream.useAttributeFor(Component.class, "granularity");
    xstream.useAttributeFor(MinecraftItem.class, "modID");
    xstream.useAttributeFor(MinecraftItem.class, "itemName");
    xstream.useAttributeFor(MinecraftItem.class, "itemMeta");
    xstream.useAttributeFor(OreDictionaryItem.class, "oreDictionary");
    xstream.alias("water", Water.class);
    xstream.alias("material", Material.class);
    xstream.alias("component", Component.class);
    xstream.alias("OreDictionaryItem", OreDictionaryItem.class);
    xstream.alias("MinecraftItem", MinecraftItem.class);

    ClassAliasingMapper mapper = new ClassAliasingMapper(xstream.getMapper());  
    mapper.addClassAlias("amount", Double.class);
    xstream.registerLocalConverter(Component.class, "granularities", new CollectionConverter(mapper));
    return xstream;
  }

  public Object readConfig(XStream xstream, String fileName) throws IOException {
    File configFile = new File(configPath, fileName);
    
    //    if(configFile.exists()) {
    //      return xstream.fromXML(configFile);
    //    }

    InputStream defaultFile = this.getClass().getResourceAsStream("/assets/enderioaddons/config/" + fileName);
    if(defaultFile == null) {
      throw new IOException("Could not get resource /assets/enderioaddons/config/" + fileName + " from classpath. ");
    }
    
    Object myObject = xstream.fromXML(defaultFile);
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(configFile, false));
      xstream.toXML(myObject, writer);
    } finally {
      IOUtils.closeQuietly(writer);
    }
    return myObject;
  }

  void go(XStream xstream) throws IOException {
    Water wc = new Water();
    wc.contents = new HashMap<String, Double>();
    wc.materials = new ArrayList<Material>();
    
    wc.contents.put("Chloride", 18980.0);
    wc.contents.put("Sodium", 10561.0);
    wc.contents.put("Aluminium", 0.001);
    
    Material m = new Material();
    m.name = "Aluminium";
    OreDictionaryItem i = new OreDictionaryItem();
    i.oreDictionary = "blockAluminium";
    m.item = i;
    m.prio = 1;
    m.volume = 1000000.0;
    wc.materials.add(m);
    m.components = new ArrayList<Component>();
    
    Component c = new Component();
    c.name = "Aluminium";
    c.factor = 1.0;
    m.density = 2.70;
    c.count = 1;
    c.granularity = 1.0;
    m.components.add(c);
    
    
    m = new Material();
    m.name = "Salt";
    MinecraftItem mi = new MinecraftItem();
    m.item = mi;
    mi.modID = "harvestcraft";
    mi.itemName = "foodSalt";
    mi.itemMeta = 0;
    m.prio = 1;
    m.volume = 1000000.0;
    wc.materials.add(m);
    m.components = new ArrayList<Component>();
    
    c = new Component();
    c.name = "Chloride";
    c.factor = 0.01;
    m.density = 2.165;
    c.count = 1;
    c.granularity = 1000.0;
    m.components.add(c);
    
    c = new Component();
    c.name = "Sodium";
    c.factor = 0.01;
    c.count = 1;
    c.granularity = 1000.0;
    m.components.add(c);
    
    
    
    // Config cfg = new Config(wc);
    
    File configFile = new File(configPath, "test.xml");
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(configFile, false));
      xstream.toXML(wc, writer);
    } finally {
      IOUtils.closeQuietly(writer);
    }
  }
  
  public void dump(XStream xstream, Config config) throws IOException {
    File configFile = new File(configPath, "dump.xml");
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(configFile, false));
      xstream.toXML(config, writer);
    } finally {
      IOUtils.closeQuietly(writer);
    }
  }

}

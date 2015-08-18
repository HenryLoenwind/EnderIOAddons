package info.loenwind.enderioaddons.common;

import info.loenwind.enderioaddons.machine.drain.BlockDrain;

import java.io.IOException;

import net.minecraft.init.Blocks;
import test.Config;
import test.TestX;
import test.Water;

import com.thoughtworks.xstream.XStream;

import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public interface InitAware {
    void init(FMLPreInitializationEvent event);

    void init(FMLInitializationEvent event);
    
    void init(FMLPostInitializationEvent event);
}

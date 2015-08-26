package info.loenwind.enderioaddons.common;

import info.loenwind.enderioaddons.EnderIOAddons;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import org.apache.commons.lang3.StringUtils;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import crazypants.enderio.fluid.BlockFluidEio;
import crazypants.enderio.fluid.ItemBucketEio;

public enum Fluids {
  BRINE1("brine1", 1250, 3000, 7), //
  BRINE2("brine2", 1500, 5000, 4), //
  BRINE3("brine3", 1750, 7000, 2), //
  BRINE4("brine4", 2000, 9000, 1);

  private final String name;
  private final int density; // kg/m³
  private final int viscosity;
  private final int quanta;
  private Fluid fluid;
  private BlockFluidEio block;
  private ItemBucketEio bucket;

  Fluids(String name, int density, int viscosity, int quanta) {
    this.name = name;
    this.density = density;
    this.viscosity = viscosity;
    this.quanta = quanta;
  }

  public static void init(FMLPreInitializationEvent event) {
    for (Fluids fluid : values()) {
      Fluid f = new Fluid(fluid.name).setDensity(fluid.density).setViscosity(fluid.viscosity);
      FluidRegistry.registerFluid(f);
      fluid.fluid = FluidRegistry.getFluid(f.getName());
      fluid.block = BlockFluidEioA.create(fluid.fluid, Material.water);
      fluid.block.setQuantaPerBlock(fluid.quanta);
      fluid.bucket = ItemBucketEio.create(fluid.fluid);
      fluid.bucket.setTextureName(EnderIOAddons.DOMAIN + ":" + "bucket" + StringUtils.capitalize(fluid.name));
      System.out.println(fluid.name + ": " + fluid.block + " and " + fluid.bucket);
    }
  }

  public Fluid getFluid() {
    return fluid;
  }

  public BlockFluidEio getBlock() {
    return block;
  }

  public ItemBucketEio getBucket() {
    return bucket;
  }

}

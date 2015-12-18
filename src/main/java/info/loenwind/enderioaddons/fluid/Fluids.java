package info.loenwind.enderioaddons.fluid;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.baseclass.BlockFluidEioA;
import info.loenwind.enderioaddons.common.NullHelper;

import javax.annotation.Nonnull;

import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.StringUtils;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import crazypants.enderio.fluid.BlockFluidEio;
import crazypants.enderio.fluid.BucketHandler;
import crazypants.enderio.fluid.ItemBucketEio;

public enum Fluids {
  BRINE1("brine1", 1250, 3000, 7, false, null), //
  BRINE2("brine2", 1500, 5000, 4, false, null), //
  BRINE3("brine3", 1750, 7000, 2, false, null), //
  BRINE4("brine4", 2000, 9000, 1, false, null), //
  MILK("milk", 1050, 2500, 6, true, Items.milk_bucket);

  @Nonnull
  private final String name;
  private final int density; // kg/m³
  private final int viscosity;
  private final int quanta;
  private final boolean allowSubstitutes;
  private Fluid fluid;
  private BlockFluidEio block;
  private Item bucket;

  Fluids(@Nonnull String name, int density, int viscosity, int quanta, boolean allowSubstitutes, Item bucket) {
    this.name = name;
    this.density = density;
    this.viscosity = viscosity;
    this.quanta = quanta;
    this.allowSubstitutes = allowSubstitutes;
    this.bucket = bucket;
  }

  public static void init(@SuppressWarnings("unused") FMLPreInitializationEvent event) {
    for (Fluids fluid : values()) {
      Fluid newFluid = new Fluid(fluid.name);
      newFluid.setDensity(fluid.density).setViscosity(fluid.viscosity);
      if (FluidRegistry.registerFluid(newFluid)) {
        fluid.fluid = newFluid;
      } else if (fluid.allowSubstitutes) {
        fluid.fluid = FluidRegistry.getFluid(fluid.name);
      } else {
        throw new RuntimeException("Failed to register fluid '" + fluid.name + "', there already is a conflicting fluid with the same name.");
      }
      fluid.block = BlockFluidEioA.create(newFluid, NullHelper.notnullF(Material.water, "Material.water"));
      fluid.block.setQuantaPerBlock(fluid.quanta);
      if (fluid.bucket == null) {
        ItemStack filled = FluidContainerRegistry.fillFluidContainer(new FluidStack(fluid.fluid, 1000), new ItemStack(Items.bucket));
        if (filled == null) {
          fluid.bucket = ItemBucketEio.create(newFluid);
          fluid.bucket.setTextureName(EnderIOAddons.DOMAIN + ":" + "bucket" + StringUtils.capitalize(fluid.name));
        } else {
          fluid.bucket = filled.getItem();
        }
      } else {
        FluidContainerRegistry.registerFluidContainer(fluid.fluid, new ItemStack(fluid.bucket), new ItemStack(Items.bucket));
        BucketHandler.instance.registerFluid(fluid.getBlock(), fluid.bucket);
      }
    }
  }

  public Fluid getFluid() {
    return fluid;
  }

  public BlockFluidEio getBlock() {
    return block;
  }

  public Item getBucket() {
    return bucket;
  }

}

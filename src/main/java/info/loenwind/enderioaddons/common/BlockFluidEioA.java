package info.loenwind.enderioaddons.common;

import info.loenwind.enderioaddons.EnderIOAddons;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.fluid.BlockFluidEio;

public class BlockFluidEioA extends BlockFluidEio {

  public static BlockFluidEio create(Fluid fluid, Material material) {
    BlockFluidEioA res = new BlockFluidEioA(fluid, material);
    res.init();
    fluid.setBlock(res);
    return res;
  }

  private BlockFluidEioA(Fluid fluid, Material material) {
    super(fluid, material);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iconRegister) {
    icons = new IIcon[] { iconRegister.registerIcon(EnderIOAddons.DOMAIN + ":" + fluidName + "_still"),
        iconRegister.registerIcon(EnderIOAddons.DOMAIN + ":" + fluidName + "_flow") };

    fluid.setIcons(icons[0], icons[1]);
  }

}

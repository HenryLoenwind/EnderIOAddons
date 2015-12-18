package info.loenwind.enderioaddons.baseclass;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.fluid.Fluids;

import java.util.Collection;

import javax.annotation.Nonnull;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.fluid.BlockFluidEio;

public class BlockFluidEioA extends BlockFluidEio {

  @Nonnull
  public static BlockFluidEio create(@Nonnull Fluid fluid, @Nonnull Material material) {
    BlockFluidEioA res = new BlockFluidEioA(fluid, material);
    res.init();
    fluid.setBlock(res);
    return res;
  }

  private BlockFluidEioA(@Nonnull Fluid fluid, @Nonnull Material material) {
    super(fluid, material);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iconRegister) {
    icons = new IIcon[] { iconRegister.registerIcon(EnderIOAddons.DOMAIN + ":" + fluidName + "_still"),
        iconRegister.registerIcon(EnderIOAddons.DOMAIN + ":" + fluidName + "_flow") };

    fluid.setIcons(icons[0], icons[1]);
  }

  @Override
  public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
    if (!entity.worldObj.isRemote) {
      if (entity instanceof EntityLivingBase && this == Fluids.MILK.getBlock()) {
        final ItemStack curativeItem = new ItemStack(Fluids.MILK.getBucket());
        if (hasCurablePotionEffect((EntityLivingBase) entity, curativeItem)) {
          if (entity instanceof EntityPlayerMP && !((EntityPlayerMP) entity).capabilities.isCreativeMode) {
            if (isSourceBlock(world, x, y, z)) {
              ((EntityLivingBase) entity).curePotionEffects(curativeItem);
              if (world.rand.nextFloat() < Config.sourceMilkIsConsumedWhenCuringPoison.getFloat()) {
                world.setBlockToAir(x, y, z);
              }
            } else {
              if (world.rand.nextFloat() < Config.flowingMilkCuresPoison.getFloat()) {
                ((EntityLivingBase) entity).curePotionEffects(curativeItem);
              }
            }
          } else {
            ((EntityLivingBase) entity).curePotionEffects(curativeItem);
          }
        }
      }
    }
    super.onEntityCollidedWithBlock(world, x, y, z, entity);
  }

  private static boolean hasCurablePotionEffect(EntityLivingBase entity, ItemStack curativeItem) {
    for (PotionEffect effect : (Collection<PotionEffect>) entity.getActivePotionEffects()) {
      if (effect.isCurativeItem(curativeItem)) {
        return true;
      }
    }
    return false;
  }

}

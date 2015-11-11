package info.loenwind.enderioaddons.machine.afarm.module.execute;

import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm.SLOT;
import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import info.loenwind.enderioaddons.machine.afarm.module.EjectSeedsModule;
import info.loenwind.enderioaddons.machine.afarm.module.IAfarmControlModule;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.ItemUtil;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import crazypants.enderio.machine.farm.PacketFarmAction;
import crazypants.enderio.network.PacketHandler;

public abstract class ExecuteModule implements IAfarmControlModule {

  public static void depositItems(WorkTile workTile, List<ItemStack> result) {
    if (result != null && !result.isEmpty()) {
      final SlotDefinitionAfarm slotDef = (SlotDefinitionAfarm) workTile.farm.getSlotDefinition();
      for (ItemStack stack : result) {
        if (stack != null && stack.stackSize > 0) {
          stack.stackSize -= ItemUtil.doInsertItem(workTile.farm, slotDef.getMinSlot(SLOT.SEED), slotDef.getMaxSlot(SLOT.SEED), stack);
        }
        if (stack != null && stack.stackSize > 0) {
          stack.stackSize -= ItemUtil.doInsertItem(workTile.farm, slotDef.getMinSlot(SLOT.CROPSTICK), slotDef.getMaxSlot(SLOT.CROPSTICK), stack);
        }
        if (stack != null && stack.stackSize > 0) {
          stack = EjectSeedsModule.putIntoOutput(workTile, stack);
        }
        if (stack != null && stack.stackSize > 0) {
          final World world = workTile.farm.getWorldObj();
          world.spawnEntityInWorld(new EntityItem(world, workTile.bc.x + 0.5, workTile.bc.y + 0.9, workTile.bc.z + 0.5, stack));
        }
      }
      workTile.farm.markDirty();
    }
  }

  public static void spawnParticles(WorkTile workTile) {
    PacketHandler.INSTANCE.sendToAllAround(new PacketFarmAction(workTile.bc), new TargetPoint(workTile.farm.getWorldObj().provider.dimensionId, workTile.bc.x,
        workTile.bc.y, workTile.bc.z, 64));
    workTile.farm.getWorldObj().playSoundEffect(workTile.bc.x + 0.5F, workTile.bc.y + 0.1F, workTile.bc.z + 0.5F,
        Blocks.farmland.stepSound.getStepResourcePath(), (Blocks.farmland.stepSound.getVolume() + 0.5F) / 2.0F, Blocks.farmland.stepSound.getPitch() * 0.8F);
  }

  public static boolean apply(ItemStack stack, EntityPlayer player, World world, BlockCoord bc) {
    BlockCoord below = bc.getLocation(ForgeDirection.DOWN);
    return stack.getItem().onItemUse(stack, player, world, below.x, below.y, below.z, 1, 0.5f, 0.5f, 0.5f);
  }

  @Override
  public int getPriority() {
    return 100;
  }

  @Override
  public boolean isCompatibleWith(IAfarmControlModule other) {
    return !(other.getClass() == this.getClass());
  }

}

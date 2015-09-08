package info.loenwind.enderioaddons.machine.waterworks;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.tool.SmartTank;

public class PacketWaterworksProgress extends MessageTileEntity<TileWaterworks> implements IMessageHandler<PacketWaterworksProgress, IMessage> {

  private float progress = 0;

  private int inputTankId = -1;
  private int inputTankAmount = -1;
  private int outputTankId = -1;
  private int outputTankAmount = -1;

  private int progress_inId = -1;
  private int progress_outId = -1;

  private float stashProgress = 0.0f;

  public PacketWaterworksProgress() {

  }

  public PacketWaterworksProgress(TileWaterworks te) {
    super(te);
    progress = te.getProgress();

    inputTankAmount = te.inputTank.getFluidAmount();
    if (inputTankAmount > 0) {
      inputTankId = te.inputTank.getFluid().getFluidID();
    }

    outputTankAmount = te.outputTank.getFluidAmount();
    if (outputTankAmount > 0) {
      outputTankId = te.outputTank.getFluid().getFluidID();
    }

    if (te.progress_in != null) {
      progress_inId = te.progress_in.getID();
    }

    if (te.progress_out != null) {
      progress_outId = te.progress_out.getID();
    }

    stashProgress = te.stashProgress;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeFloat(progress);
    buf.writeInt(inputTankId);
    buf.writeInt(inputTankAmount);
    buf.writeInt(outputTankId);
    buf.writeInt(outputTankAmount);
    buf.writeInt(progress_inId);
    buf.writeInt(progress_outId);
    buf.writeFloat(stashProgress);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    progress = buf.readFloat();
    inputTankId = buf.readInt();
    inputTankAmount = buf.readInt();
    outputTankId = buf.readInt();
    outputTankAmount = buf.readInt();
    progress_inId = buf.readInt();
    progress_outId = buf.readInt();
    stashProgress = buf.readFloat();
  }

  @Override
  public IMessage onMessage(PacketWaterworksProgress message, MessageContext ctx) {
    TileWaterworks te = message.getTileEntity(EnderIO.proxy.getClientWorld());
    if (te != null) {
      te.setProgress(message.progress);
      setTank(te.inputTank, message.inputTankAmount, message.inputTankId);
      setTank(te.outputTank, message.outputTankAmount, message.outputTankId);
      if (message.progress_inId >= 0) {
        te.progress_in = FluidRegistry.getFluid(message.progress_inId);
      }
      if (message.progress_outId >= 0) {
        te.progress_out = FluidRegistry.getFluid(message.progress_outId);
      } else {
        te.progress_out = null;
      }
      te.stashProgress = message.stashProgress;
    }
    return null;
  }

  private static void setTank(SmartTank tank, int amount, int flid) {
    if (amount > 0) {
      if (tank.getFluid() != null && flid == tank.getFluid().getFluidID()) {
        tank.setFluidAmount(amount);
      } else {
        tank.setFluid(new FluidStack(FluidRegistry.getFluid(flid), amount));
      }
    } else {
      tank.setFluid(null);
    }
  }
}

package info.loenwind.enderioaddons.machine.pmon;

import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.common.Log;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DynaTextureProvider {

  protected static final int TEXSIZE = 32;

  protected static final Queue<ResourceLocation> toFree = new ConcurrentLinkedQueue<>();
  protected static final List<DynaTextureProvider> instances = new ArrayList<>();

  protected static final ResourceLocation pmon_frame = new ResourceLocation(EnderIOAddons.DOMAIN, "textures/blocks/blockPMonFrame.png");
  protected static final int[] pmon_frame_data = new int[TEXSIZE * TEXSIZE];
  protected static final ResourceLocation pmon_color = new ResourceLocation(EnderIOAddons.DOMAIN, "textures/blocks/blockPMonColor.png");
  protected static final int[] pmon_color_data = new int[TEXSIZE * TEXSIZE];

  protected final TilePMon owner;
  protected final String id;
  protected ResourceLocation resourceLocation;
  protected final int[] imageData;

  protected final DynamicTexture dynamicTexture;
  protected final TextureManager textureManager;
  protected final IResourceManager resourceManager;

  public DynaTextureProvider(TilePMon owner) {
    this.owner = owner;
    this.textureManager = Minecraft.getMinecraft().getTextureManager();
    this.resourceManager = Minecraft.getMinecraft().getResourceManager();

    this.id = EnderIOAddons.DOMAIN + "pmon/x" + owner.xCoord + "y" + owner.yCoord + "z" + owner.zCoord;

    this.dynamicTexture = new DynamicTexture(TEXSIZE, TEXSIZE);
    this.imageData = this.dynamicTexture.getTextureData();
    this.resourceLocation = textureManager.getDynamicTextureLocation(id, this.dynamicTexture);

    for (int i = 0; i < this.imageData.length; ++i) {
      this.imageData[i] = 0;
    }
    loadTextures();
    updateTexture();
    instances.add(this);
  }

  protected static boolean texturesLoaded = false;

  protected void loadTextures() {
    if (!texturesLoaded) {
      BufferedImage pmon_frame_image = getTexture(pmon_frame);
      if (pmon_frame_image != null) {
        pmon_frame_image = resize(pmon_frame_image, TEXSIZE);//GL_BGRA TYPE_INT_ARGB
        pmon_frame_image.getRGB(0, 0, TEXSIZE, TEXSIZE, pmon_frame_data, 0, TEXSIZE);
        //        convertImageData(pmon_frame_data);
      }
      BufferedImage pmon_color_image = getTexture(pmon_color);
      if (pmon_color_image != null) {
        pmon_color_image = resize(pmon_color_image, TEXSIZE);
        pmon_color_image.getRGB(0, 0, TEXSIZE, TEXSIZE, pmon_color_data, 0, TEXSIZE);
        //        convertImageData(pmon_color_data);
      }
      texturesLoaded = true;
      MinecraftForge.EVENT_BUS.register(new Unloader());
    }
  }

  protected static void convertImageData(int[] data) {
    for (int i = 0; i < data.length; i++) {
      data[i] = //
      ((data[i] & 0xFF000000) >>> 3 * 8) | //
          ((data[i] & 0x00FF0000) >> 1 * 8) | //
          ((data[i] & 0x0000FF00) << 1 * 8) | //
          ((data[i] & 0x000000FF) << 3 * 8); //
    }
  }
  
  protected static BufferedImage resize(BufferedImage image, int size) {
    if (image.getWidth() != size) {
      BufferedImage resized = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = resized.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g.drawImage(image, 0, 0, size, size, 0, 0, image.getWidth(), image.getHeight(), null);
      g.dispose();
      return resized;
    } else {
      return image;
    }
  }

  protected BufferedImage getTexture(ResourceLocation blockResource) {
    try {
      IResource iResource = resourceManager.getResource(blockResource);
      if (iResource == null) {
        return null;
      }
      BufferedImage image = ImageIO.read(iResource.getInputStream());
      return image;
    } catch (IOException e) {
      Log.error("Failed to load " + blockResource + ": " + e);
    }
    return null;
  }

  public void updateTexture() {
    if (resourceLocation != null) {
      int[][] minmax = owner.getIconValues();

      for (int x = 0; x < TEXSIZE; x++) {
        for (int y = 0; y < TEXSIZE; y++) {
          imageData[y * TEXSIZE + x] = (TEXSIZE - y > minmax[1][x] * 23 / 63 + 4 || TEXSIZE - y < minmax[0][x] * 23 / 63 + 4 ? pmon_frame_data
              : pmon_color_data)[y * TEXSIZE + x];
        }
      }

      dynamicTexture.updateDynamicTexture();
    }
  }

  public void bindTexture() {
    if (resourceLocation != null) {
      textureManager.bindTexture(resourceLocation);
    } else {
      textureManager.bindTexture(new ResourceLocation(EnderIOAddons.DOMAIN, "textures/blocks/blockPMon.png"));
    }
  }

  public void free() {
    if (resourceLocation != null) {
      textureManager.deleteTexture(resourceLocation);
      resourceLocation = null;
    }
    ResourceLocation r = toFree.poll();
    while (r != null) {
      textureManager.deleteTexture(r);
      r = toFree.poll();
    }
  }

  @Override
  protected void finalize() throws Throwable {
    if (resourceLocation != null) {
      toFree.add(resourceLocation);
      resourceLocation = null;
    }
    super.finalize();
  }

  public static class Unloader {
    @SuppressWarnings({ "static-method", "unused" })
    @SubscribeEvent
    public void unload(WorldEvent.Unload event) {
      for (DynaTextureProvider instance : instances) {
        instance.free();
      }
      instances.clear();
    }
  }

}

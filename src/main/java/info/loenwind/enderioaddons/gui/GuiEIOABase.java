package info.loenwind.enderioaddons.gui;

import static info.loenwind.enderioaddons.EnderIOAddons.mode24;
import info.loenwind.enderioaddons.EnderIOAddons;

import java.util.Collections;
import java.util.List;

import net.minecraft.inventory.Container;
import scala.actors.threadpool.Arrays;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.AbstractPoweredMachineEntity;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;

public abstract class GuiEIOABase<T extends AbstractPoweredMachineEntity> extends GuiPoweredMachineBase<T> {

  private static final List<String> lang = Arrays.asList(new String[] { "Krismasi Njema", "UKhisimusi omuhle", "Moni Wa Chikondwelero Cha Kristmasi",
      "کرسمس مبارک", "Gëzuar Krishtlindjen", "عيد ميلاد مجيد", "Շնորհավոր Ամանոր և Սուրբ Ծնունդ", "Vrolijk Kerstfeest", "djoyeus Noyé", "'vesela 'koleda",
      "圣诞快乐", "聖誕快樂", "Sretan Božić", "Prejeme Vam Vesele Vanoce", "Glædelig Jul", "Mbotama Malamu", "Feliĉan Kristnaskon", "Rõõmsaid Jõulupühi",
      "Hyvää Joulua", "Joyeux Noël", "Nedeleg Laouen", "Bon Natale", "Frohe Weihnachten", "Ni ti Burunya Chou", "Afishapa", "Καλά Χριστούγεννα",
      "გილოცავ შობა-ახალ წელს", "Juullimi Pilluarit", "Mele Kalikimaka", "Prettig Kerstfeest", "Kellemes karácsonyi ünnepeket", "Gleðileg jól", "शुभ क्रिसमस",
      "کرسمس", "Krismasasya shubhkaamnaa", "આનંદી નાતાલ", "শুভ বড়দিন", "கிறிஸ்துமஸ் வாழ்த்துக்கள்", "Khushal Borit Natala", "ಕ್ರಿಸ್ ಮಸ್ ಹಬ್ಬದ ಶುಭಾಷಯಗಳು",
      "Krismas Chibai", "शुभ नाताळ", "ਕਰਿਸਮ ਤੇ ਨਵਾੰ ਸਾਲ ਖੁਸ਼ਿਯਾੰਵਾਲਾ ਹੋਵੇ", "Christmas inte mangalaashamsakal", "Christmas Subhakankshalu", "Selamat Natal",
      "Christmas MobArak", "Kirîsmes u ser sala we pîroz be", "Nollaig Shona Dhuit", "חג מולד שמח", "Buon Natale", "Bon Natali", "めりーくりすます", "메리 크리스마스",
      "Priecïgus Ziemassvºtkus", "Linksmų Kalėdų", "Среќен Божик", "Tratra ny Noely", "Il-Milied it-Tajjeb", "Selamat Hari Natal", "Puthuvalsara Aashamsakal",
      "Gozhqq Keshmish", "Nizhonigo Keshmish", "Quvianagli Anaiyyuniqpaliqsi", "Alussistuakeggtaarmek", "क्रस्मसको शुभकामना", "Meri Kirihimete",
      "barka dà Kirsìmatì", "E ku odun", "Jabbama be salla Kirismati", "E keresimesi Oma", "Iselogbe", "Idara ukapade isua", "God Jul", "Maligayang Pasko",
      "Naragsak Nga Paskua", "Malipayon nga Pascua", "Maayong Pasko", "Maugmang Pasko", "Maabig ya pasko", "Wesołych Świąt Bożego Narodzenia", "Feliz Natal",
      "Crặciun Fericit", "C рождеством", "Noheli nziza", "Manuia Le Kerisimasi", "Blithe Yule", "Nollaig Chridheil", "Христос се роди", "Vesele Vianoce",
      "Vesel Božič", "Kirismas Wacan", "Geseënde Kersfees", "Feliz Navidad", "Bon Nadal", "Bo Nadal", "Eguberri on", "Schöni Wiehnachte",
      "Suk sarn warn Christmas", "Mutlu Noeller", "Seku Kulu", "Веселого Різдва і з Новим Роком", "Chuć Mưǹg Giańg Sinh", "Nadolig Llawen", "Muve neKisimusi",
      "Izilokotho Ezihle Zamaholdeni", "toDwI'ma' qoS yItIvqu'", "Alassë a Hristomerendë", "Mereth Veren e-Doled Eruion", "Happy Season Greetings" });

  public GuiEIOABase(T machine, Container container) {
    super(machine, container);
  }

  @Override
  protected void updatePowerBarTooltip(List<String> text) {
    if (mode24) {
      int frame = (int) ((EnderIO.proxy.getTickCount() / 30) % (lang.size() - 1));
      if (frame == 0) {
        Collections.shuffle(lang);
      }
      text.add(lang.get(frame));
    } else {
      super.updatePowerBarTooltip(text);
    }
  }


  @Override
  public void renderPowerBar(int k, int l) {
    if (mode24) {
      if (renderPowerBar()) {
        int i1 = getPowerHeight();
        int frame = (int) ((EnderIO.proxy.getTickCount() / 3) % 25) * 10;
        RenderUtil.bindTexture(EnderIOAddons.DOMAIN + ":textures/gui/overlay.png");
        drawTexturedModalRect(k + getPowerX(), l + (getPowerY() + getPowerHeight()) - i1, frame, 0, getPowerWidth(), i1);
      }
    } else {
      super.renderPowerBar(k, l);
    }
  }

}
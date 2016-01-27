package slimeknights.tconstruct.library.tools;

import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.utils.Tags;

public class ToolNBT {
  public int durability;
  public int harvestLevel;
  public float attack;
  public float speed; // mining speed
  public int modifiers; // free modifiers

  public ToolNBT() {
    durability = 0;
    harvestLevel = 0;
    attack = 0;
    speed = 0;
  }

  public ToolNBT(NBTTagCompound tag) {
    read(tag);
  }

  /** Initialize the stats with the heads. CALL THIS FIRST */
  public ToolNBT head(HeadMaterialStats... heads) {
    durability = 0;
    harvestLevel = 0;
    attack = 0;
    speed = 0;

    // average all stats
    for(HeadMaterialStats head : heads) {
      if(head != null) {
        durability += head.durability;
        attack += head.attack;
        speed += head.miningspeed;

        // use highest harvestlevel
        if(head.harvestLevel > harvestLevel) {
          harvestLevel = head.harvestLevel;
        }
      }
    }

    durability = Math.max(1, durability/heads.length);
    attack /= (float)heads.length;
    speed /= (float)heads.length;

    return this;
  }

  /** Add stats from the accessoires. Call this second! */
  public ToolNBT extra(ExtraMaterialStats... extras) {
    int dur = 0;
    for(ExtraMaterialStats extra : extras) {
      if(extra != null) {
        dur += extra.extraDurability;
      }
    }
    this.durability += Math.round((float)dur / (float)extras.length);

    return this;
  }

  /** Calculate in handles. call this last! */
  public ToolNBT handle(HandleMaterialStats... handles) {
    // (Average Head Durability + Average Extra Durability) * Average Handle Modifier + Average Handle Durability

    int dur = 0;
    float modifier = 0f;
    for(HandleMaterialStats handle : handles) {
      if(handle != null) {
        dur += handle.durability;
        modifier += handle.handleQuality;
      }
    }

    modifier /= (float)handles.length;
    this.durability = Math.round((float)this.durability * modifier);

    // add in handle durability change
    this.durability += Math.round((float)dur / (float)handles.length);

    this.durability = Math.max(1, this.durability);

    return this;
  }

  public void read(NBTTagCompound tag) {
    durability = tag.getInteger(Tags.DURABILITY);
    harvestLevel = tag.getInteger(Tags.HARVESTLEVEL);
    attack = tag.getFloat(Tags.ATTACK);
    speed = tag.getFloat(Tags.MININGSPEED);
    modifiers = tag.getInteger(Tags.FREE_MODIFIERS);
  }

  public void write(NBTTagCompound tag) {
    tag.setInteger(Tags.DURABILITY, durability);
    tag.setInteger(Tags.HARVESTLEVEL, harvestLevel);
    tag.setFloat(Tags.ATTACK, attack);
    tag.setFloat(Tags.MININGSPEED, speed);
    tag.setInteger(Tags.FREE_MODIFIERS, modifiers);
  }

  public NBTTagCompound get() {
    NBTTagCompound tag = new NBTTagCompound();
    write(tag);

    return tag;
  }
}

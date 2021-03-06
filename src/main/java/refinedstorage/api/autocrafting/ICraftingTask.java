package refinedstorage.api.autocrafting;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import refinedstorage.api.network.INetworkMaster;

public interface ICraftingTask {
    ICraftingPattern getPattern();

    boolean update(World world, INetworkMaster network);

    void onDone(INetworkMaster network);

    void onCancelled(INetworkMaster network);

    void writeToNBT(NBTTagCompound tag);

    String getInfo();
}

package technologicalmayhem.firstmod.block.tile;

import net.minecraft.tileentity.TileEntity;

public class TilePotentiometer extends TileEntity {

    private int power = 0;

    public void increasePower()
    {
        if (power < 15)
        {
            power++;
        }
    }

    public void decreasePower()
    {
        if (power > 0)
        {
            power--;
        }
    }
}

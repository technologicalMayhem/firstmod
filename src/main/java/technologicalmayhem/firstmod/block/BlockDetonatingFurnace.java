package technologicalmayhem.firstmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import technologicalmayhem.firstmod.FirstMod;
import technologicalmayhem.firstmod.block.tile.TileDetonatingFurnace;
import technologicalmayhem.firstmod.util.EnumFurnaceIgnitionResult;
import technologicalmayhem.firstmod.util.EnumFurnacePhase;

import javax.annotation.Nullable;

public class BlockDetonatingFurnace extends Block {

    public static final PropertyEnum<EnumFurnacePhase> FURNACE_STATE = PropertyEnum.create("furnaceState", EnumFurnacePhase.class);
    private TileDetonatingFurnace tile;

    public BlockDetonatingFurnace() {
        super(Material.ROCK);
        setUnlocalizedName(FirstMod.MODID + ".detonatingfurnace");
        setRegistryName("detonatingfurnace");
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }


    //TODO:Fix item doubling bug
    @Override
    public boolean onBlockActivated(@NotNull World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            if (playerIn.getHeldItemMainhand().getItem().getClass().equals(ItemFlintAndSteel.class)) {
                EnumFurnaceIgnitionResult result = tile.ignite();
                if (result != EnumFurnaceIgnitionResult.SUCCESS) {
                    playerIn.sendMessage(new TextComponentString(result.message));
                }
            } else {
                ItemStack held = playerIn.getHeldItemMainhand();
                ItemStack result;
                if (facing == EnumFacing.UP) {
                    result = tile.insertFuel(held);
                } else {
                    result = tile.insertSmeltableItem(held);
                }
                if (result.equals(held)) {
                    playerIn.setHeldItem(EnumHand.MAIN_HAND, result);
                }
            }
        }
        playerIn.sendMessage(new TextComponentString(tile.items.serializeNBT().toString()));
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        tile = new TileDetonatingFurnace();
        return tile;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
                new ModelResourceLocation(getRegistryName(), "inventory"));
    }

}

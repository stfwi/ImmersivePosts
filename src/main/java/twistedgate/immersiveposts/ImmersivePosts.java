package twistedgate.immersiveposts;

import org.apache.logging.log4j.Logger;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.ComparableItemStack;
import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import twistedgate.immersiveposts.common.CommonProxy;
import twistedgate.immersiveposts.enums.EnumPostMaterial;
import twistedgate.immersiveposts.utils.StringUtils;

@Mod(
	modid=IPOMod.ID,
	name=IPOMod.NAME,
	dependencies=IPOMod.DEPENDS,
	certificateFingerprint=IPOMod.CERT_PRINT,
	updateJSON=IPOMod.UPDATE_URL
)
public class ImmersivePosts{
	@Mod.Instance(IPOMod.ID)
	public static ImmersivePosts instance;
	
	@SidedProxy(modId=IPOMod.ID, serverSide=IPOMod.PROXY_SERVER, clientSide=IPOMod.PROXY_CLIENT)
	public static CommonProxy proxy;
	
	public static final CreativeTabs ipCreativeTab=new CreativeTabs(IPOMod.ID){
		ItemStack iconstack=null;
		@Override
		public ItemStack createIcon(){
			if(this.iconstack==null)
				iconstack=new ItemStack(IPOStuff.postBase);
			return this.iconstack;
		}
	};
	
	public static Logger log;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		log=event.getModLog();
		
		proxy.preInit(event);
		
		//GameRegistry.registerTileEntity(TileEntityGlowy.class, new ResourceLocation(""));
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		proxy.init(event);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		proxy.postInit(event);
		
		ComparableItemStack compMoldRod = ApiUtils.createComparableItemStack(new ItemStack(IEContent.itemMold, 1, 2), false);
		for(EnumPostMaterial mat:EnumPostMaterial.values()){
			switch(mat){
				case WOOD:case NETHERBRICK:case IRON:case ALUMINIUM:case STEEL:continue;
				default:{
					String name=StringUtils.upperCaseFirst(mat.toString());
					MetalPressRecipe.addRecipe(Utils.copyStackWithAmount(IEApi.getPreferredOreStack("stick"+name), 2), "ingot"+name, compMoldRod, 2400);
				}
			}
		}
	}
}

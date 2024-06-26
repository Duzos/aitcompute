package mc.duzo.aitcompute.datagen;

import mc.duzo.aitcompute.Register;
import mc.duzo.aitcompute.common.upgrade.TurtleVortex;
import mc.duzo.aitcompute.datagen.provider.ComputedTurtleUpgradeProvider;
import mc.duzo.aitcompute.datagen.provider.lang.LanguageProvider;
import mc.duzo.aitcompute.datagen.provider.lang.LanguageType;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ComputedDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator gen) {
		FabricDataGenerator.Pack pack = gen.createPack();

		genLang(pack);
		pack.addProvider(((((output, registriesFuture) -> new ComputedTurtleUpgradeProvider(output)))));
	}

	private void genLang(FabricDataGenerator.Pack pack) {
		genEnglish(pack);
	}

	private void genEnglish(FabricDataGenerator.Pack pack) {
		pack.addProvider((((output, registriesFuture) -> {
			LanguageProvider provider = new LanguageProvider(output, LanguageType.EN_US);

			// provider.addTranslation(Register.Items.VORTEX_UPGRADE, "Vortex Upgrade");
			provider.addTranslation("upgrade.aitcompute.vortex.adjective", "Vortex");

			return provider;
		})));
	}

	private static String convertToName(String str) {
		String[] split = str.split("_");

		for (int i = 0; i < split.length; i++) {
			split[i] = split[i].substring(0, 1).toUpperCase() + split[i].substring(1).toLowerCase();
		}

		return String.join(" ", split);
	}
}

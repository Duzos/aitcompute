package mc.duzo.aitcompute.datagen;

import mc.duzo.aitcompute.datagen.provider.ComputedTurtleUpgradeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ComputedDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator gen) {
		FabricDataGenerator.Pack pack = gen.createPack();

		pack.addProvider(((((output, registriesFuture) -> new ComputedTurtleUpgradeProvider(output)))));
	}
}

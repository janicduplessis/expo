package abi43_0_0.expo.modules.imagemanipulator

import android.content.Context
import abi43_0_0.expo.modules.core.BasePackage

class ImageManipulatorPackage : BasePackage() {
  override fun createExportedModules(context: Context) = listOf(ImageManipulatorModule(context))
}

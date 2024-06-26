// Copyright 2021-present 650 Industries. All rights reserved.

#import <ABI43_0_0ExpoModulesCore/ABI43_0_0EXExportedModule.h>
#import <ABI43_0_0ExpoModulesCore/ABI43_0_0EXModuleRegistryConsumer.h>
#import <ABI43_0_0EXNotifications/ABI43_0_0EXNotificationsDelegate.h>

typedef NS_ENUM(NSUInteger, ABI43_0_0EXBackgroundNotificationResult) {
  ABI43_0_0EXBackgroundNotificationResultNoData = 1,
  ABI43_0_0EXBackgroundNotificationResultNewData = 2,
  ABI43_0_0EXBackgroundNotificationResultFailed = 3,
};

@interface ABI43_0_0EXBackgroundNotificationTasksModule : ABI43_0_0EXExportedModule <ABI43_0_0EXModuleRegistryConsumer>


@end

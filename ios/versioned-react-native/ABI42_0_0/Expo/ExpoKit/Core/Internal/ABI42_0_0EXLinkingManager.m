// Copyright 2015-present 650 Industries. All rights reserved.

#import "ABI42_0_0EXLinkingManager.h"
#import "ABI42_0_0EXScopedModuleRegistry.h"
#import "ABI42_0_0EXUtil.h"

#import <ABI42_0_0React/ABI42_0_0RCTBridge.h>
#import <ABI42_0_0React/ABI42_0_0RCTEventDispatcher.h>
#import <ABI42_0_0React/ABI42_0_0RCTUtils.h>

NSString * const ABI42_0_0EXLinkingEventOpenUrl = @"url";

@interface ABI42_0_0EXLinkingManager ()

@property (nonatomic, weak) id <ABI42_0_0EXLinkingManagerScopedModuleDelegate> kernelLinkingDelegate;
@property (nonatomic, strong) NSURL *initialUrl;
@property (nonatomic) BOOL hasListeners;

@end

@implementation ABI42_0_0EXLinkingManager

ABI42_0_0EX_EXPORT_SCOPED_MODULE(ABI42_0_0RCTLinkingManager, KernelLinkingManager);

- (instancetype)initWithExperienceStableLegacyId:(NSString *)experienceStableLegacyId
                                        scopeKey:(NSString *)scopeKey
                                    easProjectId:(NSString *)easProjectId
                           kernelServiceDelegate:(id)kernelServiceInstance
                                          params:(NSDictionary *)params
{
  if (self = [super initWithExperienceStableLegacyId:experienceStableLegacyId
                                            scopeKey:scopeKey
                                        easProjectId:easProjectId
                               kernelServiceDelegate:kernelServiceInstance
                                              params:params]) {
    _kernelLinkingDelegate = kernelServiceInstance;
    _initialUrl = params[@"initialUri"];
    if (_initialUrl == [NSNull null]) {
      _initialUrl = nil;
    }
  }
  return self;
}

#pragma mark - ABI42_0_0RCTEventEmitter methods

- (NSArray<NSString *> *)supportedEvents
{
  return @[ABI42_0_0EXLinkingEventOpenUrl];
}

- (void)startObserving
{
  _hasListeners = YES;
}

- (void)stopObserving
{
  _hasListeners = NO;
}

#pragma mark - Linking methods

- (void)dispatchOpenUrlEvent:(NSURL *)url
{
  if (!url || !url.absoluteString) {
    ABI42_0_0RCTFatal(ABI42_0_0RCTErrorWithMessage([NSString stringWithFormat:@"Tried to open a deep link to an invalid url: %@", url]));
    return;
  }
  if (_hasListeners) {
    [self sendEventWithName:ABI42_0_0EXLinkingEventOpenUrl body:@{@"url": url.absoluteString}];
  }
}

ABI42_0_0RCT_EXPORT_METHOD(openURL:(NSURL *)URL
                  resolve:(ABI42_0_0RCTPromiseResolveBlock)resolve
                  reject:(ABI42_0_0RCTPromiseRejectBlock)reject)
{
  if ([_kernelLinkingDelegate linkingModule:self shouldOpenExpoUrl:URL]) {
    [_kernelLinkingDelegate linkingModule:self didOpenUrl:URL.absoluteString];
    resolve(@YES);
  } else {
    [ABI42_0_0EXUtil performSynchronouslyOnMainThread:^{
      [ABI42_0_0RCTSharedApplication() openURL:URL options:@{} completionHandler:^(BOOL success) {
        if (success) {
          resolve(nil);
        } else {
          reject(ABI42_0_0RCTErrorUnspecified, [NSString stringWithFormat:@"Unable to open URL: %@", URL], nil);
        }
      }];
    }];
  }
}

ABI42_0_0RCT_EXPORT_METHOD(canOpenURL:(NSURL *)URL
                  resolve:(ABI42_0_0RCTPromiseResolveBlock)resolve
                  reject:(__unused ABI42_0_0RCTPromiseRejectBlock)reject)
{
  __block BOOL canOpen = [_kernelLinkingDelegate linkingModule:self shouldOpenExpoUrl:URL];
  if (!canOpen) {
    [ABI42_0_0EXUtil performSynchronouslyOnMainThread:^{
      canOpen = [ABI42_0_0RCTSharedApplication() canOpenURL:URL];
    }];
  }
  resolve(@(canOpen));
}

ABI42_0_0RCT_EXPORT_METHOD(openSettings:(ABI42_0_0RCTPromiseResolveBlock)resolve
                        reject:(ABI42_0_0RCTPromiseRejectBlock)reject)
{
  [ABI42_0_0EXUtil performSynchronouslyOnMainThread:^{
    NSURL *url = [NSURL URLWithString:UIApplicationOpenSettingsURLString];
    [ABI42_0_0RCTSharedApplication() openURL:url options:@{} completionHandler:^(BOOL success) {
      if (success) {
        resolve(nil);
      } else {
        reject(ABI42_0_0RCTErrorUnspecified, @"Unable to open app settings", nil);
      }
    }];
  }];
}

ABI42_0_0RCT_EXPORT_METHOD(getInitialURL:(ABI42_0_0RCTPromiseResolveBlock)resolve
                  reject:(__unused ABI42_0_0RCTPromiseRejectBlock)reject)
{
  resolve(ABI42_0_0RCTNullIfNil(_initialUrl.absoluteString));
}

@end

import Constants from 'expo-constants';
import * as SMS from 'expo-sms';

import { expectMethodToThrowAsync } from '../TestUtils';
import { isInteractive } from '../utils/Environment';
import {
  loadAttachmentsAsync,
  cleanupAttachmentsAsync,
  testSMSComposeWithSingleImageAttachment,
  testSMSComposeWithTwoImageAttachments,
  testSMSComposeWithAudioAttachment,
  testSMSComposeWithNullRecipient,
  testSMSComposeWithUndefinedRecipient,
} from './SMSCommon';

export const name = 'SMS';

export function test({ describe, it, expect, beforeAll, afterAll }) {
  describe('SMS', () => {
    describe(`sendSMSAsync()`, () => {
      if (Constants.isDevice) {
        if (isInteractive()) {
          beforeAll(() => loadAttachmentsAsync(expect));

          it(`opens an SMS composer`, async () => {
            // TODO: Bacon: Build an API to close the UI Controller
            await SMS.sendSMSAsync(['0123456789', '9876543210'], 'test');
          });

          it(`opens an SMS composer with single image attachment`, async () => {
            await testSMSComposeWithSingleImageAttachment(expect);
          });

          it(`opens an SMS composer with two image attachments`, async () => {
            await testSMSComposeWithTwoImageAttachments(expect);
          });

          it(`opens an SMS composer with audio attachment`, async () => {
            await testSMSComposeWithAudioAttachment(expect);
          });

          it(`throws when provided with undefined recipient`, async () => {
            const error = await expectMethodToThrowAsync(testSMSComposeWithUndefinedRecipient);
            expect(error.message).toBe('undefined or null address');
          });

          it(`throws when provided with null recipient`, async () => {
            const error = await expectMethodToThrowAsync(testSMSComposeWithNullRecipient);
            expect(error.message).toBe('undefined or null address');
          });

          afterAll(() => cleanupAttachmentsAsync(expect));
        }
      } else {
        it(`is unavailable`, async () => {
          const error = await expectMethodToThrowAsync(SMS.sendSMSAsync);
          expect(error.code).toBe('E_SMS_UNAVAILABLE');
        });
      }
    });

    describe(`isAvailableAsync()`, () => {
      if (Constants.isDevice) {
        it(`has access to iOS SMS API`, async () => {
          expect(await SMS.isAvailableAsync()).toBe(true);
        });
      } else {
        it(`cannot be used in the iOS simulator`, async () => {
          expect(await SMS.isAvailableAsync()).toBe(false);
        });
      }
    });
  });
}

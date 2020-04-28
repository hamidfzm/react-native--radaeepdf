#import "Radaeepdf.h"

@implementation Radaeepdf

RCT_EXPORT_MODULE()

RCT_REMAP_METHOD(Show,
            path:
            (NSString *) path
            password:
            (NSString *) password
            findEventsWithResolver:
            (RCTPromiseResolveBlock) resolve
            Rejecter:
            (RCTPromiseRejectBlock) reject) {
    NSLog(@"%@ %@", path, password);
    resolve(@"OK");
}

RCT_REMAP_METHOD(Activate,
            type:
            (NSNumber *) type
            company:
            (NSString *) company
            mail:
            (NSString *) mail
            key:
            (NSString *) key
            findEventsWithResolver:
            (RCTPromiseResolveBlock) resolve
            Rejecter:
            (RCTPromiseRejectBlock) reject) {
    NSLog(@"%@ %@ %@ %@", type, company, mail, key);
    resolve(@"OK");
}

@end

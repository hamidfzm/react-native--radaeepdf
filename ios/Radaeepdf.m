#import "Radaeepdf.h"

#import "RDVGlobal.h"

@implementation Radaeepdf

RCT_EXPORT_MODULE()

- (id)init
{
  self = [super init];

  NSLog(@"Init RadaeePDFPlugin");
  plugin = [RadaeePDFPlugin pluginInit];

  return self;
}

RCT_EXPORT_METHOD(Show:
            (NSString *) path
            password:
            (NSString *) password) {
    [plugin show:path withPassword:password];
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
    g_id = [[NSBundle mainBundle] bundleIdentifier];
    g_company = company;
    g_mail = mail;
    g_serial = key;
    [RDVGlobal Init];
    
    resolve(@"OK");
}

@end

import { NativeModules } from 'react-native';

import { License } from './types';

const { Radaeepdf } = NativeModules;

const Show = (path: string, password: string = '') => {
  Radaeepdf.Show(path, password);
};

const Activate = (license: License) => {
  return new Promise((resolve, reject) => {
    Radaeepdf.Activate(
      license.type,
      license.company,
      license.mail,
      license.key,
      (activated: boolean) => {
        if (activated) {
          resolve(activated);
        }
        reject(activated);
      }
    );
  });
};

export default {
  Show,
  Activate,
};

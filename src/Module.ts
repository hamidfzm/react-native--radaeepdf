import { NativeModules } from 'react-native';

import { License } from './types';

const { Radaeepdf } = NativeModules;

const Show = (path: string, password: string = '') => {
  Radaeepdf.Show(path, password);
};

const Activate = (license: License): Promise<string | boolean> => {
  return Radaeepdf.Activate(
    license.type,
    license.company,
    license.mail,
    license.key
  );
};

export default {
  Show,
  Activate,
};

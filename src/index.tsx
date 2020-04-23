import { NativeModules } from 'react-native';

type RadaeepdfType = {
  getDeviceName(): Promise<string>;
};

const { Radaeepdf } = NativeModules;

export default Radaeepdf as RadaeepdfType;

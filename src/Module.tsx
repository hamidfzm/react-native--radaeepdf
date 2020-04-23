import { License } from './types';

const Show = (path: string, password?: string) => console.log(path, password);

const Activate = (license: License) => console.log(license);

export default {
  Show,
  Activate,
};

import type { NativeSyntheticEvent } from 'react-native';
import type { Markup, Mode } from './constants';

export type License = {
  type: number;
  company: string;
  mail: string;
  key: string;
};

export type PDFViewRef = {
  setSelect: () => void;
  setSelMarkup: (type: keyof typeof Markup) => void;
  undo: () => void;
  redo: () => void;
  save: () => void;
  removeAnnot: () => void;
};

export type PDFView = {
  mode: keyof typeof Mode;
  source: {
    uri: string;
    password?: string;
    page: number;
  };
  debug: boolean;
  initialPage: number;
  onPageChange?: (page: number) => void;
  onPageModified: (page: number) => void;
  onError: () => void;
  onBlankTap: (x: number, y: number) => void;
  onDoubleTap: (x: number, y: number) => void;
  onLongPress: (x: number, y: number) => void;
  onSelectEnd: (x: number, y: number, text: string) => void;
  onAnnotTap: (x: number, y: number, selected: boolean) => void;
};

export type EventHandler<T extends NativeSyntheticEvent<any>> = (
  event: T
) => void;

export type Position = { x: number; y: number };
export type ChangeEvent = NativeSyntheticEvent<{ page: number }>;
export type ModifyEvent = NativeSyntheticEvent<{ page: number }>;
export type BlankTapEvent = NativeSyntheticEvent<Position>;
export type DoubleTapEvent = NativeSyntheticEvent<Position>;
export type LongPressEvent = NativeSyntheticEvent<Position>;
export type AnnotTapEvent = NativeSyntheticEvent<
  Position & { selected: boolean }
>;
export type SelectEndEvent = NativeSyntheticEvent<Position & { text: string }>;

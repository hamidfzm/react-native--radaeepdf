import React from 'react';
import {
  requireNativeComponent,
  findNodeHandle,
  UIManager,
  PixelRatio,
} from 'react-native';
import { View, Platform } from 'react-native';
import {
  PDFView as Props,
  ChangeEvent,
  EventHandler,
  ModifyEvent,
  DoubleTapEvent,
  BlankTapEvent,
  LongPressEvent,
  SelectEndEvent,
  AnnotTapEvent,
  PDFViewRef as Ref,
} from './types';
import { Markup, Mode } from './constants';

const uiViewClassName = 'RadaeepdfView';
const RNView =
  Platform.OS === 'ios' ? View : requireNativeComponent<any>(uiViewClassName);

const getLayoutSizeForPixelSize = (pixelSize: number) =>
  Math.round(pixelSize / PixelRatio.get());

const PDFView: React.ForwardRefRenderFunction<Ref, Props> = (
  {
    onPageChange,
    onPageModified,
    onBlankTap,
    onDoubleTap,
    onAnnotTap,
    onLongPress,
    onSelectEnd,
    mode,
    ...rest
  },
  ref
) => {
  const nativeRef = React.useRef(null);

  const handleChange = React.useCallback<EventHandler<ChangeEvent>>(
    event => {
      onPageChange?.(event.nativeEvent.page);
    },
    [onPageChange]
  );

  const handleModified = React.useCallback<EventHandler<ModifyEvent>>(
    event => {
      onPageModified(event.nativeEvent.page);
    },
    [onPageModified]
  );

  const handleBlankTap = React.useCallback<EventHandler<BlankTapEvent>>(() => {
    onBlankTap?.(0, 0);
  }, [onBlankTap]);

  const handleDoubleTap = React.useCallback<EventHandler<DoubleTapEvent>>(
    event => {
      const {
        nativeEvent: { x, y },
      } = event;
      onDoubleTap?.(getLayoutSizeForPixelSize(x), getLayoutSizeForPixelSize(y));
    },
    [onDoubleTap]
  );

  const handleLongPress = React.useCallback<EventHandler<LongPressEvent>>(
    event => {
      const {
        nativeEvent: { x, y },
      } = event;
      onLongPress?.(getLayoutSizeForPixelSize(x), getLayoutSizeForPixelSize(y));
    },
    [onLongPress]
  );

  const handleSelectEnd = React.useCallback<EventHandler<SelectEndEvent>>(
    event => {
      const {
        nativeEvent: { text, x, y },
      } = event;
      onSelectEnd?.(
        getLayoutSizeForPixelSize(x),
        getLayoutSizeForPixelSize(y),
        text
      );
    },
    [onSelectEnd]
  );

  const handleAnnotTap = React.useCallback<EventHandler<AnnotTapEvent>>(
    event => {
      const {
        nativeEvent: { x, y, selected },
      } = event;
      onAnnotTap?.(
        getLayoutSizeForPixelSize(x),
        getLayoutSizeForPixelSize(y),
        selected
      );
    },
    [onAnnotTap]
  );

  const handleNativeMethod = React.useCallback(async (method, args = []) => {
    if (nativeRef.current) {
      const handle = findNodeHandle(nativeRef.current);
      if (!handle) {
        throw new Error('Cannot find node handles');
      }

      await Platform.select({
        android: async () => {
          return UIManager.dispatchViewManagerCommand(
            findNodeHandle(nativeRef.current),
            UIManager.getViewManagerConfig(uiViewClassName).Commands[method],
            args
          );
        },
        ios: async () => {
          // return NativeModules.PDFViewManager.reload(handle);
        },
        default: async () => {},
      })();
    }
  }, []);

  React.useImperativeHandle(
    ref,
    () => ({
      setSelect: () => handleNativeMethod('setSelect'),
      setSelMarkup: type => handleNativeMethod('setSelMarkup', [Markup[type]]),
      undo: () => handleNativeMethod('undo'),
      redo: () => handleNativeMethod('redo'),
      save: () => handleNativeMethod('save'),
      removeAnnot: () => handleNativeMethod('removeAnnot'),
    }),
    [handleNativeMethod]
  );
  return (
    <RNView
      {...rest}
      mode={Mode[mode]}
      ref={nativeRef}
      onPageChange={handleChange}
      onPageModified={handleModified}
      onBlankTap={handleBlankTap}
      onDoubleTap={handleDoubleTap}
      onLongPress={handleLongPress}
      onSelectEnd={handleSelectEnd}
      onAnnotTap={handleAnnotTap}
    />
  );
};

export default React.forwardRef(PDFView);

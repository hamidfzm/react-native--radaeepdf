import * as React from 'react';
import { View, Text, Button } from 'react-native';
import Radaeepdf from 'react-native-radaeepdf';

import styles from './styles';

export default function App() {
  const handlePress = React.useCallback(() => {
    Radaeepdf.Show(
      'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf'
    );
  }, []);
  return (
    <View style={styles.container}>
      <Text>React Native RadaeePDF</Text>
      <Button title="Open PDFManager" onPress={handlePress} />
    </View>
  );
}

var React = require('react');
var {
    Component,
    PropTypes
} = React;

var ReactNative = require('react-native');
var {
    AppRegistry,
    StyleSheet,
    Text,
    View,
    Image,
} = ReactNative;


import BlurView from 'react-native-vunun-blur';

var background = 'http://iphonewallpapers-hd.com/thumbs/firework_iphone_wallpaper_5-t2.jpg';

class Example extends Component {
    
    render() {
        return (
            <Image source={{uri: background }} style={styles.container}>
                <BlurView blurType="light" style={styles.container}>
                    <Text style={styles.welcome}>Blur light</Text>
                </BlurView>
                <BlurView blurType="xlight" style={styles.container}>
                    <Text style={styles.welcome}>Blur xlight</Text>
                </BlurView>
                <BlurView blurType="dark" style={styles.container}>
                    <Text style={styles.welcome}>Blur dark</Text>
                </BlurView>
            </Image>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        backgroundColor: 'transparent',
    },
    welcome: {
        fontSize: 20,
        textAlign: 'center',
        margin: 10,
        color: '#FFFFFF',
    },
});

module.exports =  Example;

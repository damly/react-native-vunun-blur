const React = require('react');
const {
  Component,
  PropTypes
} = React;

const {View} = require('react-native');

const { requireNativeComponent } = require('react-native');

class BlurView extends Component {
  render() {
    return (
      <NativeBlurView
        {...this.props}
        style={[{
          backgroundColor: 'transparent',
        }, this.props.style
        ]}
      />
    );
  }
}

BlurView.propTypes = {
  ...View.propTypes,
  blurType: PropTypes.string,
};

const NativeBlurView = requireNativeComponent('BlurView', BlurView);

module.exports = BlurView;

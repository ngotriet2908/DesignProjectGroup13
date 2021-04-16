var path = require("path");
var values = require('postcss-modules-values');

module.exports = {
  entry: [path.resolve(__dirname, './index.js')],
  output: {
    // where compiled files go
    path: path.resolve(__dirname, '../../../../../resources/static/built'),
    filename: 'bundle.js' // the same one we import in home.html
  },
  // mode: 'development',
  mode: 'production',
  module: {
    rules: [
      {
        test: /\.js$/,
        enforce: 'pre',
        use: ['source-map-loader']
      },
      {
        test: /\.(mjs|js|jsx)$/,
        exclude: /(node_modules|bower_components)/,
        use: [{
          loader: 'babel-loader',
          options: {
            presets: ['@babel/preset-env',
              '@babel/react',
              {
                'plugins': ['@babel/plugin-proposal-class-properties']
              }
            ]
          }
        },
        ]
      },
      {
        test: /\.css$/,
        exclude: /\.module\.css/,
        use: [ 'style-loader', 'css-loader' ]
      },
      {
        test: /\.(module.css|sass|scss)$/,
        use: [
          'style-loader',
          {
            loader: 'css-loader',
            options: {
              modules: true
            }
          },
          {
            loader: 'postcss-loader',
            options: {
              postcssOptions: {
                plugins: [
                  [
                    'postcss-modules-values',
                    {
                      // Options
                    },
                  ],
                ],
              },
            }
          },
          'sass-loader',
        ]
      },
    ]
  },
  devServer: {
    writeToDisk: true
  }
};
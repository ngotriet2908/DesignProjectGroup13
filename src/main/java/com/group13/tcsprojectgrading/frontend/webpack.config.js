var path = require("path");

module.exports = {
  // entry: './rootReducer.js',
  // devtool: 'nosources',

  entry: [path.resolve(__dirname, './index.js')],
  output: {
    // where compiled files go
    path: path.resolve(__dirname, '../../../../../resources/static/built'),

    // 127.0.0.1/static/frontend/public/ where files are served from
    // publicPath: '/static/frontend/',
    filename: 'bundle.js' // the same one we import in home.html
  },
  mode: 'development',
  module: {
    // configuration regarding modules
    rules: [
      {
        test: /\.js$/,
        enforce: 'pre',
        use: ['source-map-loader']
      },
      {
        // regex test for js and jsx files
        test: /\.(js|jsx|mjs)?$/,
        // don't look in any node_modules/ or bower_components/ folders
        exclude: /(node_modules|bower_components)/,
        // for matching files, use the babel-loader
        use: {
          loader: 'babel-loader',
          options: {
            presets: ['@babel/preset-env',
              '@babel/react',
              {
                'plugins': ['@babel/plugin-proposal-class-properties']
              }
            ]
          }
        }
      },
      // {
      //   test: /plugin\.css$/,
      //   use: ['style-loader', 'css-loader'],
      // },
      {
        test: /\.(sass|css|scss)$/,
        // exclude: /node_modules/,
        include: [
          path.resolve(__dirname, 'node_modules/react-loader-spinner/dist/loader/css/'),
          path.resolve(__dirname, 'node_modules/@draft-js-plugins/static-toolbar/lib/'),
          path.resolve(__dirname, 'node_modules/draft-js/dist/'),
          path.resolve(__dirname, 'node_modules/@draft-js-plugins/hashtag/lib/'),
          path.resolve(__dirname, 'src/'),
        ],
        use: [
          'style-loader',
          {
            loader: 'css-loader',
            options: {
              modules: true
            }
          },
          'sass-loader'
        ]
      },
    ]
  },
  devServer: {
    writeToDisk: true
  }
};
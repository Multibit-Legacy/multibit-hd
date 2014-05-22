import com.google.common.base.Splitter;
import org.multibit.hd.core.config.Configurations;

import java.io.IOException;

/**
 * <p>Various tools to provide the following to Font Awesome upgrades:</p>
 * <ul>
 * <li>Converting the variables.less into an enum</li>
 * <li>Ordering keys across all variables</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class FontAwesomeTools {

  /**
   * Copy paste this from the variables.less version of Font Awesome that you are upgrading to
   */
  private static String variablesLessRaw = "@fa-var-adjust: \"\\f042\";\n" +
    "@fa-var-adn: \"\\f170\";\n" +
    "@fa-var-align-center: \"\\f037\";\n" +
    "@fa-var-align-justify: \"\\f039\";\n" +
    "@fa-var-align-left: \"\\f036\";\n" +
    "@fa-var-align-right: \"\\f038\";\n" +
    "@fa-var-ambulance: \"\\f0f9\";\n" +
    "@fa-var-anchor: \"\\f13d\";\n" +
    "@fa-var-android: \"\\f17b\";\n" +
    "@fa-var-angle-double-down: \"\\f103\";\n" +
    "@fa-var-angle-double-left: \"\\f100\";\n" +
    "@fa-var-angle-double-right: \"\\f101\";\n" +
    "@fa-var-angle-double-up: \"\\f102\";\n" +
    "@fa-var-angle-down: \"\\f107\";\n" +
    "@fa-var-angle-left: \"\\f104\";\n" +
    "@fa-var-angle-right: \"\\f105\";\n" +
    "@fa-var-angle-up: \"\\f106\";\n" +
    "@fa-var-apple: \"\\f179\";\n" +
    "@fa-var-archive: \"\\f187\";\n" +
    "@fa-var-arrow-circle-down: \"\\f0ab\";\n" +
    "@fa-var-arrow-circle-left: \"\\f0a8\";\n" +
    "@fa-var-arrow-circle-o-down: \"\\f01a\";\n" +
    "@fa-var-arrow-circle-o-left: \"\\f190\";\n" +
    "@fa-var-arrow-circle-o-right: \"\\f18e\";\n" +
    "@fa-var-arrow-circle-o-up: \"\\f01b\";\n" +
    "@fa-var-arrow-circle-right: \"\\f0a9\";\n" +
    "@fa-var-arrow-circle-up: \"\\f0aa\";\n" +
    "@fa-var-arrow-down: \"\\f063\";\n" +
    "@fa-var-arrow-left: \"\\f060\";\n" +
    "@fa-var-arrow-right: \"\\f061\";\n" +
    "@fa-var-arrow-up: \"\\f062\";\n" +
    "@fa-var-arrows: \"\\f047\";\n" +
    "@fa-var-arrows-alt: \"\\f0b2\";\n" +
    "@fa-var-arrows-h: \"\\f07e\";\n" +
    "@fa-var-arrows-v: \"\\f07d\";\n" +
    "@fa-var-asterisk: \"\\f069\";\n" +
    "@fa-var-automobile: \"\\f1b9\";\n" +
    "@fa-var-backward: \"\\f04a\";\n" +
    "@fa-var-ban: \"\\f05e\";\n" +
    "@fa-var-bank: \"\\f19c\";\n" +
    "@fa-var-bar-chart-o: \"\\f080\";\n" +
    "@fa-var-barcode: \"\\f02a\";\n" +
    "@fa-var-bars: \"\\f0c9\";\n" +
    "@fa-var-beer: \"\\f0fc\";\n" +
    "@fa-var-behance: \"\\f1b4\";\n" +
    "@fa-var-behance-square: \"\\f1b5\";\n" +
    "@fa-var-bell: \"\\f0f3\";\n" +
    "@fa-var-bell-o: \"\\f0a2\";\n" +
    "@fa-var-bitbucket: \"\\f171\";\n" +
    "@fa-var-bitbucket-square: \"\\f172\";\n" +
    "@fa-var-bitcoin: \"\\f15a\";\n" +
    "@fa-var-bold: \"\\f032\";\n" +
    "@fa-var-bolt: \"\\f0e7\";\n" +
    "@fa-var-bomb: \"\\f1e2\";\n" +
    "@fa-var-book: \"\\f02d\";\n" +
    "@fa-var-bookmark: \"\\f02e\";\n" +
    "@fa-var-bookmark-o: \"\\f097\";\n" +
    "@fa-var-briefcase: \"\\f0b1\";\n" +
    "@fa-var-btc: \"\\f15a\";\n" +
    "@fa-var-bug: \"\\f188\";\n" +
    "@fa-var-building: \"\\f1ad\";\n" +
    "@fa-var-building-o: \"\\f0f7\";\n" +
    "@fa-var-bullhorn: \"\\f0a1\";\n" +
    "@fa-var-bullseye: \"\\f140\";\n" +
    "@fa-var-cab: \"\\f1ba\";\n" +
    "@fa-var-calendar: \"\\f073\";\n" +
    "@fa-var-calendar-o: \"\\f133\";\n" +
    "@fa-var-camera: \"\\f030\";\n" +
    "@fa-var-camera-retro: \"\\f083\";\n" +
    "@fa-var-car: \"\\f1b9\";\n" +
    "@fa-var-caret-down: \"\\f0d7\";\n" +
    "@fa-var-caret-left: \"\\f0d9\";\n" +
    "@fa-var-caret-right: \"\\f0da\";\n" +
    "@fa-var-caret-square-o-down: \"\\f150\";\n" +
    "@fa-var-caret-square-o-left: \"\\f191\";\n" +
    "@fa-var-caret-square-o-right: \"\\f152\";\n" +
    "@fa-var-caret-square-o-up: \"\\f151\";\n" +
    "@fa-var-caret-up: \"\\f0d8\";\n" +
    "@fa-var-certificate: \"\\f0a3\";\n" +
    "@fa-var-chain: \"\\f0c1\";\n" +
    "@fa-var-chain-broken: \"\\f127\";\n" +
    "@fa-var-check: \"\\f00c\";\n" +
    "@fa-var-check-circle: \"\\f058\";\n" +
    "@fa-var-check-circle-o: \"\\f05d\";\n" +
    "@fa-var-check-square: \"\\f14a\";\n" +
    "@fa-var-check-square-o: \"\\f046\";\n" +
    "@fa-var-chevron-circle-down: \"\\f13a\";\n" +
    "@fa-var-chevron-circle-left: \"\\f137\";\n" +
    "@fa-var-chevron-circle-right: \"\\f138\";\n" +
    "@fa-var-chevron-circle-up: \"\\f139\";\n" +
    "@fa-var-chevron-down: \"\\f078\";\n" +
    "@fa-var-chevron-left: \"\\f053\";\n" +
    "@fa-var-chevron-right: \"\\f054\";\n" +
    "@fa-var-chevron-up: \"\\f077\";\n" +
    "@fa-var-child: \"\\f1ae\";\n" +
    "@fa-var-circle: \"\\f111\";\n" +
    "@fa-var-circle-o: \"\\f10c\";\n" +
    "@fa-var-circle-o-notch: \"\\f1ce\";\n" +
    "@fa-var-circle-thin: \"\\f1db\";\n" +
    "@fa-var-clipboard: \"\\f0ea\";\n" +
    "@fa-var-clock-o: \"\\f017\";\n" +
    "@fa-var-cloud: \"\\f0c2\";\n" +
    "@fa-var-cloud-download: \"\\f0ed\";\n" +
    "@fa-var-cloud-upload: \"\\f0ee\";\n" +
    "@fa-var-cny: \"\\f157\";\n" +
    "@fa-var-code: \"\\f121\";\n" +
    "@fa-var-code-fork: \"\\f126\";\n" +
    "@fa-var-codepen: \"\\f1cb\";\n" +
    "@fa-var-coffee: \"\\f0f4\";\n" +
    "@fa-var-cog: \"\\f013\";\n" +
    "@fa-var-cogs: \"\\f085\";\n" +
    "@fa-var-columns: \"\\f0db\";\n" +
    "@fa-var-comment: \"\\f075\";\n" +
    "@fa-var-comment-o: \"\\f0e5\";\n" +
    "@fa-var-comments: \"\\f086\";\n" +
    "@fa-var-comments-o: \"\\f0e6\";\n" +
    "@fa-var-compass: \"\\f14e\";\n" +
    "@fa-var-compress: \"\\f066\";\n" +
    "@fa-var-copy: \"\\f0c5\";\n" +
    "@fa-var-credit-card: \"\\f09d\";\n" +
    "@fa-var-crop: \"\\f125\";\n" +
    "@fa-var-crosshairs: \"\\f05b\";\n" +
    "@fa-var-css3: \"\\f13c\";\n" +
    "@fa-var-cube: \"\\f1b2\";\n" +
    "@fa-var-cubes: \"\\f1b3\";\n" +
    "@fa-var-cut: \"\\f0c4\";\n" +
    "@fa-var-cutlery: \"\\f0f5\";\n" +
    "@fa-var-dashboard: \"\\f0e4\";\n" +
    "@fa-var-database: \"\\f1c0\";\n" +
    "@fa-var-dedent: \"\\f03b\";\n" +
    "@fa-var-delicious: \"\\f1a5\";\n" +
    "@fa-var-desktop: \"\\f108\";\n" +
    "@fa-var-deviantart: \"\\f1bd\";\n" +
    "@fa-var-digg: \"\\f1a6\";\n" +
    "@fa-var-dollar: \"\\f155\";\n" +
    "@fa-var-dot-circle-o: \"\\f192\";\n" +
    "@fa-var-download: \"\\f019\";\n" +
    "@fa-var-dribbble: \"\\f17d\";\n" +
    "@fa-var-dropbox: \"\\f16b\";\n" +
    "@fa-var-drupal: \"\\f1a9\";\n" +
    "@fa-var-edit: \"\\f044\";\n" +
    "@fa-var-eject: \"\\f052\";\n" +
    "@fa-var-ellipsis-h: \"\\f141\";\n" +
    "@fa-var-ellipsis-v: \"\\f142\";\n" +
    "@fa-var-empire: \"\\f1d1\";\n" +
    "@fa-var-envelope: \"\\f0e0\";\n" +
    "@fa-var-envelope-o: \"\\f003\";\n" +
    "@fa-var-envelope-square: \"\\f199\";\n" +
    "@fa-var-eraser: \"\\f12d\";\n" +
    "@fa-var-eur: \"\\f153\";\n" +
    "@fa-var-euro: \"\\f153\";\n" +
    "@fa-var-exchange: \"\\f0ec\";\n" +
    "@fa-var-exclamation: \"\\f12a\";\n" +
    "@fa-var-exclamation-circle: \"\\f06a\";\n" +
    "@fa-var-exclamation-triangle: \"\\f071\";\n" +
    "@fa-var-expand: \"\\f065\";\n" +
    "@fa-var-external-link: \"\\f08e\";\n" +
    "@fa-var-external-link-square: \"\\f14c\";\n" +
    "@fa-var-eye: \"\\f06e\";\n" +
    "@fa-var-eye-slash: \"\\f070\";\n" +
    "@fa-var-facebook: \"\\f09a\";\n" +
    "@fa-var-facebook-square: \"\\f082\";\n" +
    "@fa-var-fast-backward: \"\\f049\";\n" +
    "@fa-var-fast-forward: \"\\f050\";\n" +
    "@fa-var-fax: \"\\f1ac\";\n" +
    "@fa-var-female: \"\\f182\";\n" +
    "@fa-var-fighter-jet: \"\\f0fb\";\n" +
    "@fa-var-file: \"\\f15b\";\n" +
    "@fa-var-file-archive-o: \"\\f1c6\";\n" +
    "@fa-var-file-audio-o: \"\\f1c7\";\n" +
    "@fa-var-file-code-o: \"\\f1c9\";\n" +
    "@fa-var-file-excel-o: \"\\f1c3\";\n" +
    "@fa-var-file-image-o: \"\\f1c5\";\n" +
    "@fa-var-file-movie-o: \"\\f1c8\";\n" +
    "@fa-var-file-o: \"\\f016\";\n" +
    "@fa-var-file-pdf-o: \"\\f1c1\";\n" +
    "@fa-var-file-photo-o: \"\\f1c5\";\n" +
    "@fa-var-file-picture-o: \"\\f1c5\";\n" +
    "@fa-var-file-powerpoint-o: \"\\f1c4\";\n" +
    "@fa-var-file-sound-o: \"\\f1c7\";\n" +
    "@fa-var-file-text: \"\\f15c\";\n" +
    "@fa-var-file-text-o: \"\\f0f6\";\n" +
    "@fa-var-file-video-o: \"\\f1c8\";\n" +
    "@fa-var-file-word-o: \"\\f1c2\";\n" +
    "@fa-var-file-zip-o: \"\\f1c6\";\n" +
    "@fa-var-files-o: \"\\f0c5\";\n" +
    "@fa-var-film: \"\\f008\";\n" +
    "@fa-var-filter: \"\\f0b0\";\n" +
    "@fa-var-fire: \"\\f06d\";\n" +
    "@fa-var-fire-extinguisher: \"\\f134\";\n" +
    "@fa-var-flag: \"\\f024\";\n" +
    "@fa-var-flag-checkered: \"\\f11e\";\n" +
    "@fa-var-flag-o: \"\\f11d\";\n" +
    "@fa-var-flash: \"\\f0e7\";\n" +
    "@fa-var-flask: \"\\f0c3\";\n" +
    "@fa-var-flickr: \"\\f16e\";\n" +
    "@fa-var-floppy-o: \"\\f0c7\";\n" +
    "@fa-var-folder: \"\\f07b\";\n" +
    "@fa-var-folder-o: \"\\f114\";\n" +
    "@fa-var-folder-open: \"\\f07c\";\n" +
    "@fa-var-folder-open-o: \"\\f115\";\n" +
    "@fa-var-font: \"\\f031\";\n" +
    "@fa-var-forward: \"\\f04e\";\n" +
    "@fa-var-foursquare: \"\\f180\";\n" +
    "@fa-var-frown-o: \"\\f119\";\n" +
    "@fa-var-gamepad: \"\\f11b\";\n" +
    "@fa-var-gavel: \"\\f0e3\";\n" +
    "@fa-var-gbp: \"\\f154\";\n" +
    "@fa-var-ge: \"\\f1d1\";\n" +
    "@fa-var-gear: \"\\f013\";\n" +
    "@fa-var-gears: \"\\f085\";\n" +
    "@fa-var-gift: \"\\f06b\";\n" +
    "@fa-var-git: \"\\f1d3\";\n" +
    "@fa-var-git-square: \"\\f1d2\";\n" +
    "@fa-var-github: \"\\f09b\";\n" +
    "@fa-var-github-alt: \"\\f113\";\n" +
    "@fa-var-github-square: \"\\f092\";\n" +
    "@fa-var-gittip: \"\\f184\";\n" +
    "@fa-var-glass: \"\\f000\";\n" +
    "@fa-var-globe: \"\\f0ac\";\n" +
    "@fa-var-google: \"\\f1a0\";\n" +
    "@fa-var-google-plus: \"\\f0d5\";\n" +
    "@fa-var-google-plus-square: \"\\f0d4\";\n" +
    "@fa-var-graduation-cap: \"\\f19d\";\n" +
    "@fa-var-group: \"\\f0c0\";\n" +
    "@fa-var-h-square: \"\\f0fd\";\n" +
    "@fa-var-hacker-news: \"\\f1d4\";\n" +
    "@fa-var-hand-o-down: \"\\f0a7\";\n" +
    "@fa-var-hand-o-left: \"\\f0a5\";\n" +
    "@fa-var-hand-o-right: \"\\f0a4\";\n" +
    "@fa-var-hand-o-up: \"\\f0a6\";\n" +
    "@fa-var-hdd-o: \"\\f0a0\";\n" +
    "@fa-var-header: \"\\f1dc\";\n" +
    "@fa-var-headphones: \"\\f025\";\n" +
    "@fa-var-heart: \"\\f004\";\n" +
    "@fa-var-heart-o: \"\\f08a\";\n" +
    "@fa-var-history: \"\\f1da\";\n" +
    "@fa-var-home: \"\\f015\";\n" +
    "@fa-var-hospital-o: \"\\f0f8\";\n" +
    "@fa-var-html5: \"\\f13b\";\n" +
    "@fa-var-image: \"\\f03e\";\n" +
    "@fa-var-inbox: \"\\f01c\";\n" +
    "@fa-var-indent: \"\\f03c\";\n" +
    "@fa-var-info: \"\\f129\";\n" +
    "@fa-var-info-circle: \"\\f05a\";\n" +
    "@fa-var-inr: \"\\f156\";\n" +
    "@fa-var-instagram: \"\\f16d\";\n" +
    "@fa-var-institution: \"\\f19c\";\n" +
    "@fa-var-italic: \"\\f033\";\n" +
    "@fa-var-joomla: \"\\f1aa\";\n" +
    "@fa-var-jpy: \"\\f157\";\n" +
    "@fa-var-jsfiddle: \"\\f1cc\";\n" +
    "@fa-var-key: \"\\f084\";\n" +
    "@fa-var-keyboard-o: \"\\f11c\";\n" +
    "@fa-var-krw: \"\\f159\";\n" +
    "@fa-var-language: \"\\f1ab\";\n" +
    "@fa-var-laptop: \"\\f109\";\n" +
    "@fa-var-leaf: \"\\f06c\";\n" +
    "@fa-var-legal: \"\\f0e3\";\n" +
    "@fa-var-lemon-o: \"\\f094\";\n" +
    "@fa-var-level-down: \"\\f149\";\n" +
    "@fa-var-level-up: \"\\f148\";\n" +
    "@fa-var-life-bouy: \"\\f1cd\";\n" +
    "@fa-var-life-ring: \"\\f1cd\";\n" +
    "@fa-var-life-saver: \"\\f1cd\";\n" +
    "@fa-var-lightbulb-o: \"\\f0eb\";\n" +
    "@fa-var-link: \"\\f0c1\";\n" +
    "@fa-var-linkedin: \"\\f0e1\";\n" +
    "@fa-var-linkedin-square: \"\\f08c\";\n" +
    "@fa-var-linux: \"\\f17c\";\n" +
    "@fa-var-list: \"\\f03a\";\n" +
    "@fa-var-list-alt: \"\\f022\";\n" +
    "@fa-var-list-ol: \"\\f0cb\";\n" +
    "@fa-var-list-ul: \"\\f0ca\";\n" +
    "@fa-var-location-arrow: \"\\f124\";\n" +
    "@fa-var-lock: \"\\f023\";\n" +
    "@fa-var-long-arrow-down: \"\\f175\";\n" +
    "@fa-var-long-arrow-left: \"\\f177\";\n" +
    "@fa-var-long-arrow-right: \"\\f178\";\n" +
    "@fa-var-long-arrow-up: \"\\f176\";\n" +
    "@fa-var-magic: \"\\f0d0\";\n" +
    "@fa-var-magnet: \"\\f076\";\n" +
    "@fa-var-mail-forward: \"\\f064\";\n" +
    "@fa-var-mail-reply: \"\\f112\";\n" +
    "@fa-var-mail-reply-all: \"\\f122\";\n" +
    "@fa-var-male: \"\\f183\";\n" +
    "@fa-var-map-marker: \"\\f041\";\n" +
    "@fa-var-maxcdn: \"\\f136\";\n" +
    "@fa-var-medkit: \"\\f0fa\";\n" +
    "@fa-var-meh-o: \"\\f11a\";\n" +
    "@fa-var-microphone: \"\\f130\";\n" +
    "@fa-var-microphone-slash: \"\\f131\";\n" +
    "@fa-var-minus: \"\\f068\";\n" +
    "@fa-var-minus-circle: \"\\f056\";\n" +
    "@fa-var-minus-square: \"\\f146\";\n" +
    "@fa-var-minus-square-o: \"\\f147\";\n" +
    "@fa-var-mobile: \"\\f10b\";\n" +
    "@fa-var-mobile-phone: \"\\f10b\";\n" +
    "@fa-var-money: \"\\f0d6\";\n" +
    "@fa-var-moon-o: \"\\f186\";\n" +
    "@fa-var-mortar-board: \"\\f19d\";\n" +
    "@fa-var-music: \"\\f001\";\n" +
    "@fa-var-navicon: \"\\f0c9\";\n" +
    "@fa-var-openid: \"\\f19b\";\n" +
    "@fa-var-outdent: \"\\f03b\";\n" +
    "@fa-var-pagelines: \"\\f18c\";\n" +
    "@fa-var-paper-plane: \"\\f1d8\";\n" +
    "@fa-var-paper-plane-o: \"\\f1d9\";\n" +
    "@fa-var-paperclip: \"\\f0c6\";\n" +
    "@fa-var-paragraph: \"\\f1dd\";\n" +
    "@fa-var-paste: \"\\f0ea\";\n" +
    "@fa-var-pause: \"\\f04c\";\n" +
    "@fa-var-paw: \"\\f1b0\";\n" +
    "@fa-var-pencil: \"\\f040\";\n" +
    "@fa-var-pencil-square: \"\\f14b\";\n" +
    "@fa-var-pencil-square-o: \"\\f044\";\n" +
    "@fa-var-phone: \"\\f095\";\n" +
    "@fa-var-phone-square: \"\\f098\";\n" +
    "@fa-var-photo: \"\\f03e\";\n" +
    "@fa-var-picture-o: \"\\f03e\";\n" +
    "@fa-var-pied-piper: \"\\f1a7\";\n" +
    "@fa-var-pied-piper-alt: \"\\f1a8\";\n" +
    "@fa-var-pied-piper-square: \"\\f1a7\";\n" +
    "@fa-var-pinterest: \"\\f0d2\";\n" +
    "@fa-var-pinterest-square: \"\\f0d3\";\n" +
    "@fa-var-plane: \"\\f072\";\n" +
    "@fa-var-play: \"\\f04b\";\n" +
    "@fa-var-play-circle: \"\\f144\";\n" +
    "@fa-var-play-circle-o: \"\\f01d\";\n" +
    "@fa-var-plus: \"\\f067\";\n" +
    "@fa-var-plus-circle: \"\\f055\";\n" +
    "@fa-var-plus-square: \"\\f0fe\";\n" +
    "@fa-var-plus-square-o: \"\\f196\";\n" +
    "@fa-var-power-off: \"\\f011\";\n" +
    "@fa-var-print: \"\\f02f\";\n" +
    "@fa-var-puzzle-piece: \"\\f12e\";\n" +
    "@fa-var-qq: \"\\f1d6\";\n" +
    "@fa-var-qrcode: \"\\f029\";\n" +
    "@fa-var-question: \"\\f128\";\n" +
    "@fa-var-question-circle: \"\\f059\";\n" +
    "@fa-var-quote-left: \"\\f10d\";\n" +
    "@fa-var-quote-right: \"\\f10e\";\n" +
    "@fa-var-ra: \"\\f1d0\";\n" +
    "@fa-var-random: \"\\f074\";\n" +
    "@fa-var-rebel: \"\\f1d0\";\n" +
    "@fa-var-recycle: \"\\f1b8\";\n" +
    "@fa-var-reddit: \"\\f1a1\";\n" +
    "@fa-var-reddit-square: \"\\f1a2\";\n" +
    "@fa-var-refresh: \"\\f021\";\n" +
    "@fa-var-renren: \"\\f18b\";\n" +
    "@fa-var-reorder: \"\\f0c9\";\n" +
    "@fa-var-repeat: \"\\f01e\";\n" +
    "@fa-var-reply: \"\\f112\";\n" +
    "@fa-var-reply-all: \"\\f122\";\n" +
    "@fa-var-retweet: \"\\f079\";\n" +
    "@fa-var-rmb: \"\\f157\";\n" +
    "@fa-var-road: \"\\f018\";\n" +
    "@fa-var-rocket: \"\\f135\";\n" +
    "@fa-var-rotate-left: \"\\f0e2\";\n" +
    "@fa-var-rotate-right: \"\\f01e\";\n" +
    "@fa-var-rouble: \"\\f158\";\n" +
    "@fa-var-rss: \"\\f09e\";\n" +
    "@fa-var-rss-square: \"\\f143\";\n" +
    "@fa-var-rub: \"\\f158\";\n" +
    "@fa-var-ruble: \"\\f158\";\n" +
    "@fa-var-rupee: \"\\f156\";\n" +
    "@fa-var-save: \"\\f0c7\";\n" +
    "@fa-var-scissors: \"\\f0c4\";\n" +
    "@fa-var-search: \"\\f002\";\n" +
    "@fa-var-search-minus: \"\\f010\";\n" +
    "@fa-var-search-plus: \"\\f00e\";\n" +
    "@fa-var-send: \"\\f1d8\";\n" +
    "@fa-var-send-o: \"\\f1d9\";\n" +
    "@fa-var-share: \"\\f064\";\n" +
    "@fa-var-share-alt: \"\\f1e0\";\n" +
    "@fa-var-share-alt-square: \"\\f1e1\";\n" +
    "@fa-var-share-square: \"\\f14d\";\n" +
    "@fa-var-share-square-o: \"\\f045\";\n" +
    "@fa-var-shield: \"\\f132\";\n" +
    "@fa-var-shopping-cart: \"\\f07a\";\n" +
    "@fa-var-sign-in: \"\\f090\";\n" +
    "@fa-var-sign-out: \"\\f08b\";\n" +
    "@fa-var-signal: \"\\f012\";\n" +
    "@fa-var-sitemap: \"\\f0e8\";\n" +
    "@fa-var-skype: \"\\f17e\";\n" +
    "@fa-var-slack: \"\\f198\";\n" +
    "@fa-var-sliders: \"\\f1de\";\n" +
    "@fa-var-smile-o: \"\\f118\";\n" +
    "@fa-var-sort: \"\\f0dc\";\n" +
    "@fa-var-sort-alpha-asc: \"\\f15d\";\n" +
    "@fa-var-sort-alpha-desc: \"\\f15e\";\n" +
    "@fa-var-sort-amount-asc: \"\\f160\";\n" +
    "@fa-var-sort-amount-desc: \"\\f161\";\n" +
    "@fa-var-sort-asc: \"\\f0de\";\n" +
    "@fa-var-sort-desc: \"\\f0dd\";\n" +
    "@fa-var-sort-down: \"\\f0dd\";\n" +
    "@fa-var-sort-numeric-asc: \"\\f162\";\n" +
    "@fa-var-sort-numeric-desc: \"\\f163\";\n" +
    "@fa-var-sort-up: \"\\f0de\";\n" +
    "@fa-var-soundcloud: \"\\f1be\";\n" +
    "@fa-var-space-shuttle: \"\\f197\";\n" +
    "@fa-var-spinner: \"\\f110\";\n" +
    "@fa-var-spoon: \"\\f1b1\";\n" +
    "@fa-var-spotify: \"\\f1bc\";\n" +
    "@fa-var-square: \"\\f0c8\";\n" +
    "@fa-var-square-o: \"\\f096\";\n" +
    "@fa-var-stack-exchange: \"\\f18d\";\n" +
    "@fa-var-stack-overflow: \"\\f16c\";\n" +
    "@fa-var-star: \"\\f005\";\n" +
    "@fa-var-star-half: \"\\f089\";\n" +
    "@fa-var-star-half-empty: \"\\f123\";\n" +
    "@fa-var-star-half-full: \"\\f123\";\n" +
    "@fa-var-star-half-o: \"\\f123\";\n" +
    "@fa-var-star-o: \"\\f006\";\n" +
    "@fa-var-steam: \"\\f1b6\";\n" +
    "@fa-var-steam-square: \"\\f1b7\";\n" +
    "@fa-var-step-backward: \"\\f048\";\n" +
    "@fa-var-step-forward: \"\\f051\";\n" +
    "@fa-var-stethoscope: \"\\f0f1\";\n" +
    "@fa-var-stop: \"\\f04d\";\n" +
    "@fa-var-strikethrough: \"\\f0cc\";\n" +
    "@fa-var-stumbleupon: \"\\f1a4\";\n" +
    "@fa-var-stumbleupon-circle: \"\\f1a3\";\n" +
    "@fa-var-subscript: \"\\f12c\";\n" +
    "@fa-var-suitcase: \"\\f0f2\";\n" +
    "@fa-var-sun-o: \"\\f185\";\n" +
    "@fa-var-superscript: \"\\f12b\";\n" +
    "@fa-var-support: \"\\f1cd\";\n" +
    "@fa-var-table: \"\\f0ce\";\n" +
    "@fa-var-tablet: \"\\f10a\";\n" +
    "@fa-var-tachometer: \"\\f0e4\";\n" +
    "@fa-var-tag: \"\\f02b\";\n" +
    "@fa-var-tags: \"\\f02c\";\n" +
    "@fa-var-tasks: \"\\f0ae\";\n" +
    "@fa-var-taxi: \"\\f1ba\";\n" +
    "@fa-var-tencent-weibo: \"\\f1d5\";\n" +
    "@fa-var-terminal: \"\\f120\";\n" +
    "@fa-var-text-height: \"\\f034\";\n" +
    "@fa-var-text-width: \"\\f035\";\n" +
    "@fa-var-th: \"\\f00a\";\n" +
    "@fa-var-th-large: \"\\f009\";\n" +
    "@fa-var-th-list: \"\\f00b\";\n" +
    "@fa-var-thumb-tack: \"\\f08d\";\n" +
    "@fa-var-thumbs-down: \"\\f165\";\n" +
    "@fa-var-thumbs-o-down: \"\\f088\";\n" +
    "@fa-var-thumbs-o-up: \"\\f087\";\n" +
    "@fa-var-thumbs-up: \"\\f164\";\n" +
    "@fa-var-ticket: \"\\f145\";\n" +
    "@fa-var-times: \"\\f00d\";\n" +
    "@fa-var-times-circle: \"\\f057\";\n" +
    "@fa-var-times-circle-o: \"\\f05c\";\n" +
    "@fa-var-tint: \"\\f043\";\n" +
    "@fa-var-toggle-down: \"\\f150\";\n" +
    "@fa-var-toggle-left: \"\\f191\";\n" +
    "@fa-var-toggle-right: \"\\f152\";\n" +
    "@fa-var-toggle-up: \"\\f151\";\n" +
    "@fa-var-trash-o: \"\\f014\";\n" +
    "@fa-var-tree: \"\\f1bb\";\n" +
    "@fa-var-trello: \"\\f181\";\n" +
    "@fa-var-trophy: \"\\f091\";\n" +
    "@fa-var-truck: \"\\f0d1\";\n" +
    "@fa-var-try: \"\\f195\";\n" +
    "@fa-var-tumblr: \"\\f173\";\n" +
    "@fa-var-tumblr-square: \"\\f174\";\n" +
    "@fa-var-turkish-lira: \"\\f195\";\n" +
    "@fa-var-twitter: \"\\f099\";\n" +
    "@fa-var-twitter-square: \"\\f081\";\n" +
    "@fa-var-umbrella: \"\\f0e9\";\n" +
    "@fa-var-underline: \"\\f0cd\";\n" +
    "@fa-var-undo: \"\\f0e2\";\n" +
    "@fa-var-university: \"\\f19c\";\n" +
    "@fa-var-unlink: \"\\f127\";\n" +
    "@fa-var-unlock: \"\\f09c\";\n" +
    "@fa-var-unlock-alt: \"\\f13e\";\n" +
    "@fa-var-unsorted: \"\\f0dc\";\n" +
    "@fa-var-upload: \"\\f093\";\n" +
    "@fa-var-usd: \"\\f155\";\n" +
    "@fa-var-user: \"\\f007\";\n" +
    "@fa-var-user-md: \"\\f0f0\";\n" +
    "@fa-var-users: \"\\f0c0\";\n" +
    "@fa-var-video-camera: \"\\f03d\";\n" +
    "@fa-var-vimeo-square: \"\\f194\";\n" +
    "@fa-var-vine: \"\\f1ca\";\n" +
    "@fa-var-vk: \"\\f189\";\n" +
    "@fa-var-volume-down: \"\\f027\";\n" +
    "@fa-var-volume-off: \"\\f026\";\n" +
    "@fa-var-volume-up: \"\\f028\";\n" +
    "@fa-var-warning: \"\\f071\";\n" +
    "@fa-var-wechat: \"\\f1d7\";\n" +
    "@fa-var-weibo: \"\\f18a\";\n" +
    "@fa-var-weixin: \"\\f1d7\";\n" +
    "@fa-var-wheelchair: \"\\f193\";\n" +
    "@fa-var-windows: \"\\f17a\";\n" +
    "@fa-var-won: \"\\f159\";\n" +
    "@fa-var-wordpress: \"\\f19a\";\n" +
    "@fa-var-wrench: \"\\f0ad\";\n" +
    "@fa-var-xing: \"\\f168\";\n" +
    "@fa-var-xing-square: \"\\f169\";\n" +
    "@fa-var-yahoo: \"\\f19e\";\n" +
    "@fa-var-yen: \"\\f157\";\n" +
    "@fa-var-youtube: \"\\f167\";\n" +
    "@fa-var-youtube-play: \"\\f16a\";\n" +
    "@fa-var-youtube-square: \"\\f166\";\n"
    ;

  public static void main(String[] args) throws IOException {

    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    convertToEnumEntries();

  }

  /**
   * <p>Converts the raw LESS entry into an enum entry.</p>
   * <p><code>@fa-var-bitcoin: "\f15a";</code> becomes <code>BITCOIN("\uf15a"),</code></p>
   */
  private static void convertToEnumEntries() {

    Iterable<String> variablesLessRawIterable = Splitter.on("\n").split(variablesLessRaw);

    for (String variablesLessRawEntry: variablesLessRawIterable) {

      String entry = variablesLessRawEntry
        .toUpperCase()
        .replace("-","_")
        .replace("\"","'")
        .replace("@FA_VAR_","")
        .replace(": ","(")
        .replace("\\F","\\uf")
        .replace(";","),");

      System.out.println(entry);

    }

    System.out.println("  // End of enum\n;\n\nCopy paste the above into AwesomeIcon to replace the existing entries.");

  }


}

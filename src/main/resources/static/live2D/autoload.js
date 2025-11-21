/*!
 * Live2D Widget
 * https://github.com/stevenjoezhang/live2d-widget
 */

// Recommended to use absolute path for live2d_path parameter
// live2d_path 参数建议使用绝对路径
const live2d_path = 'https://fastly.jsdelivr.net/npm/live2d-widgets@1.0.0-rc.6/dist/';
// const live2d_path = '/dist/';

// Method to encapsulate asynchronous resource loading
// 封装异步加载资源的方法
function loadExternalResource(url, type) {
  return new Promise((resolve, reject) => {
    let tag;

    if (type === 'css') {
      tag = document.createElement('link');
      tag.rel = 'stylesheet';
      tag.href = url;
    }
    else if (type === 'js') {
      tag = document.createElement('script');
      tag.type = 'module';
      tag.src = url;
    }
    if (tag) {
      tag.onload = () => resolve(url);
      tag.onerror = () => reject(url);
      document.head.appendChild(tag);
    }
  });
}

(async () => {
  // If you are concerned about display issues on mobile devices, you can use screen.width to determine whether to load
  // 如果担心手机上显示效果不佳，可以根据屏幕宽度来判断是否加载
  // if (screen.width < 768) return;

  // Avoid cross-origin issues with image resources
  // 避免图片资源跨域问题
  const OriginalImage = window.Image;
  window.Image = function(...args) {
    const img = new OriginalImage(...args);
    img.crossOrigin = "anonymous";
    return img;
  };
  window.Image.prototype = OriginalImage.prototype;
  // Load waifu.css and waifu-tips.js
  // 加载 waifu.css 和 waifu-tips.js
  await Promise.all([
    loadExternalResource('/live2D/waifu.css', 'css'),
    loadExternalResource('/live2D/waifu-tips.js', 'js')
  ]);

  // 新增：延迟1秒（1000ms），确保waifu-tips.js完全加载执行
  await new Promise(resolve => setTimeout(resolve, 1000));

  // For detailed usage of configuration options, see README.en.md
  // 配置选项的具体用法见 README.md
  initWidget({
    waifuPath: '/live2D/waifu-tips.json',
    cdnPath: 'https://cdn.jsdelivr.net/gh/fengdeyan/static-resource@v1.0.0',
    cubism2Path:  '/live2D/live2d.min.js',
    cubism5Path: 'https://cubism.live2d.com/sdk-web/cubismcore/live2dcubismcore.min.js',
    tools: ['hitokoto', 'asteroids', 'switch-model', 'switch-texture', 'photo', 'info', 'quit'],
    logLevel: 'warn',
    drag: false,
  });
  initWaifuMouseEvent();
  // 新增：自动触发一次自带按钮的悬浮（兜底激活）
  setTimeout(() => {
    // 替换为你看板娘自带按钮的实际选择器（比如hitokoto按钮）
    const triggerBtn = document.querySelector("#waifu-tool-hitokoto");
    if (triggerBtn) {
      triggerBtn.dispatchEvent(new MouseEvent("mouseover"));
      triggerBtn.dispatchEvent(new MouseEvent("mouseout"));
    }
  }, 1200); // 延迟1.2秒，确保看板娘已渲染完成
})();
function initWaifuMouseEvent() {
  const waifu = document.getElementById("waifu");
  let isDown = false;
  let waifuLeft;
  let mouseLeft;
  let waifuTop;
  let mouseTop;
  // 鼠标点击监听
  waifu.onmousedown = function (e) {
    isDown = true;
    // 记录x轴
    waifuLeft = waifu.offsetLeft;
    mouseLeft = e.clientX;
    // 记录y轴
    waifuTop = waifu.offsetTop;
    mouseTop = e.clientY;
  }
  // 鼠标移动监听
  window.onmousemove = function (e) {
    if (!isDown) {
      return;
    }
    // x轴移动
    let currentLeft = waifuLeft + (e.clientX - mouseLeft);
    if (currentLeft < 0) {
      currentLeft = 0;
    } else if (currentLeft > window.innerWidth - 300) {
      currentLeft = window.innerWidth - 300;
    }
    waifu.style.left = currentLeft  + "px";
  }
  // 鼠标点击松开监听
  window.onmouseup = function (e) {
    isDown = false;
  }
}

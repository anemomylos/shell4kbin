package net.easyjoin.utils;

public final class Constants
{
  public static final String profileNameKey = "profileName";
  public static final String injectJSKey = "injectJSKey";
  public static final String injectJSTextKey = "injectJSTextKey";
  public static final String injectCSSKey = "injectCSSKey";
  public static final String injectCSSTextKey = "injectCSSTextKey";
  public static final String fontSizeKey = "fontSizeKey";

  /*https://greasyfork.org/en/scripts/468612-kbin-enhancement-script/code*/
  public static final String jsSource = "!function(){let e=document.createElement(\"style\");e.textContent=`\n" +
    "    .entry figure {overflow: hidden}\n" +
    "    .comment .badge {padding:.25rem;margin-left:5px}\n" +
    "    #kes-version-dialog {position: fixed; width: 100vw; height: 100vh; top: 0; left: 0; display: flex; align-items: center; justify-content: center; background-color: rgba(0,0,0,.3); z-index: 9999999}\n" +
    "    .kes-version-dialog-content {background: #ddd; color: #444; position: relative; padding: 40px}\n" +
    "\t\t.kes-expand {grid-gap: 0; padding-left:55px}\n" +
    "    .kes-blur {filter: blur(4px); transition-duration: 0.5s}\n" +
    "\t\t.kes-blur-large {filter: blur(15px)}\n" +
    "    .kes-blur:hover {filter: none}\n" +
    "  `,document.head.appendChild(e);let t=[{name:\"Show domains\",value:\"show-domains\"},{name:\"Show collapse comment\",value:\"show-collapse\"},{name:\"Show collapse replies\",value:\"show-collapse-replies\"},{name:\"Replies start collapsed\",value:\"start-collapse-replies\",default:\"false\"},{name:\"Move comment box to top\",value:\"comment-box-top\"},{name:\"Reply box improvements\",value:\"comment-cancel\"},{name:\"Hide known NSFW domains\",value:\"nsfw-hide\"},{name:\"Blur known NSFW domains\",value:\"nsfw-blur\"},{name:\"Hide random sidebar posts\",value:\"hide-random\"},{name:\"Add OP tag\",value:\"op-tag\"}];function l(e){let t=localStorage.getItem(\"setting-\"+e);return null===t&&(t=\"true\"),\"true\"===t}function n(e,t){localStorage.setItem(\"setting-\"+e,t),location.reload()}function o(e){let t=e.title.split(\"@\");if(t[2]!==location.hostname&&!e.innerText.includes(\"@\"+t[2])){let l=e.childNodes[e.childNodes.length-1];l.nodeValue+=\"@\"+t[2]}}function i(e,t){let l=e.id.split(\"-\")[2];t.push(e);let n=e.parentElement.querySelectorAll('blockquote[data-subject-parent-value=\"'+l+'\"]');n.forEach(e=>{i(e,t)})}function r(e){let t=[];!function e(t,l){let n=t.id.split(\"-\")[2];t.classList.contains(\"kes-expand\")&&l.push(t);let o=t.parentElement.querySelectorAll('blockquote[data-subject-parent-value=\"'+n+'\"]');o.forEach(t=>{e(t,l)})}(e,t),t.forEach(e=>{e.remove()})}function a(e){let t=e.id.split(\"-\")[2],l=\"1\";e.classList.forEach(e=>{e.includes(\"comment-level\")&&(l=e.split(\"--\")[1])});let n=[];i(e,n),n.forEach(e=>{e.style.display=\"none\"});let o=e.querySelector(\"header a\").innerText,a=e.querySelector(\"header time\").innerText,s=document.createElement(\"blockquote\");s.className=\"kes-expand section comment entry-comment comment-level--\"+l,s.dataset.subjectParentValue=t,s.innerHTML='<header><a href=\"javascript:;\">'+o+\", \"+a+\" [+]</a></header>\",s.querySelector(\"a\").addEventListener(\"click\",()=>{!function e(t){let l=[];i(t,l),l.forEach(e=>{e.style.display=\"\"}),r(t)}(e)}),e.parentNode.insertBefore(s,e)}function s(e){let t=e.querySelector(\"div.actions ul\"),l=document.createElement(\"li\");l.innerHTML='<div><button class=\"btn btn__primary\">Cancel</button></div>',t.appendChild(l),l.querySelector(\"button\").addEventListener(\"click\",()=>{var t;(t=e).innerHTML=\"\",t.style=\"\"})}function c(e){let t=e.action.split(\"/\"),l=\"entry_comment_body_\"+t[t.length-1];e.querySelector(\"#entry_comment_body\").id=l,e.querySelector(\"markdown-toolbar\").setAttribute(\"for\",l)}t.forEach(e=>{\"false\"===e.default&&null===localStorage.getItem(\"setting-\"+e.value)&&localStorage.setItem(\"setting-\"+e.value,\"false\")});let d=[\"lemmynsfw.com\",\"redgifs.com\"];if(!function e(){let o=document.querySelector(\".settings-list\"),i=document.createElement(\"strong\");i.textContent=\"kbin enhancement script\",o.appendChild(i),t.forEach(e=>{o.appendChild(function e(t,n){let o=l(n),i=document.createElement(\"div\");return i.className=\"row\",i.innerHTML=`<span>${t}:</span>\n" +
    "      <div>\n" +
    "        <a class=\"kes-setting-yes link-muted ${o?\"active\":\"\"}\" href=\"javascript:;\" data-setting=\"${n}\">\n" +
    "          Yes\n" +
    "        </a>\n" +
    "        |\n" +
    "        <a class=\"kes-setting-no link-muted ${o?\"\":\"active\"}\" href=\"javascript:;\" data-setting=\"${n}\">\n" +
    "          No\n" +
    "        </a>\n" +
    "      </div>`,i}(e.name,e.value))}),document.querySelectorAll(\".kes-setting-yes\").forEach(e=>{e.addEventListener(\"click\",()=>{n(e.dataset.setting,!0)})}),document.querySelectorAll(\".kes-setting-no\").forEach(e=>{e.addEventListener(\"click\",()=>{n(e.dataset.setting,!1)})})}(),l(\"show-domains\")&&function e(){document.querySelectorAll(\".magazine-inline, .user-inline\").forEach(e=>{o(e)});let t=(e,t)=>{for(let l of e)l.addedNodes.forEach(e=>{e.querySelectorAll(\".magazine-inline, .user-inline\").forEach(e=>{o(e)})})},l=new MutationObserver(t),n=document.querySelector(\"div#content > div\");n&&l.observe(n,{childList:!0})}(),l(\"show-collapse\")&&function e(){if(location.pathname.startsWith(\"/m\")){let t=document.querySelectorAll(\"blockquote.comment\");t.forEach(e=>{let t=e.querySelector(\"header\");if(!t.innerText.includes(\"[-]\")){let l=document.createElement(\"a\");l.href=\"javascript:;\",l.className=\"kes-collapse\",l.innerHTML=\"[-]\",t.appendChild(l)}}),document.querySelectorAll(\".kes-collapse\").forEach(e=>{e.addEventListener(\"click\",()=>{let t=e.closest(\"blockquote.comment\");a(t)})})}}(),l(\"show-collapse-replies\")&&function e(){if(location.pathname.startsWith(\"/m\")){let t=document.querySelectorAll(\"blockquote.comment-level--1\");t.forEach(e=>{let t=e.id.split(\"-\")[2],l=e.parentElement.querySelectorAll('blockquote[data-subject-parent-value=\"'+t+'\"]');if(l.length>0){let n=e.querySelector(\"footer menu\"),o=document.createElement(\"li\");o.innerHTML='<a href=\"javascript:;\" class=\"kes-collapse-replies\">toggle replies ('+function e(t){let l=[];return i(t,l),l.length-1}(e)+\")</a>\",n.appendChild(o)}}),document.querySelectorAll(\".kes-collapse-replies\").forEach(e=>{e.addEventListener(\"click\",()=>{let t=e.closest(\"blockquote.comment\");!function e(t,l){t.id.split(\"-\")[2];let n=function e(t){let l=[];return i(t,l),l.splice(l.indexOf(t),1),l}(t),o=!1;n.forEach(e=>{\"none\"==e.style.display&&(o=!0)}),n.forEach(e=>{e.style.display=o?\"\":\"none\"}),r(t)}(t)})})}}(),l(\"start-collapse-replies\")&&function e(){let t=document.querySelectorAll(\"blockquote.comment-level--2\");t.forEach(e=>{a(e)})}(),l(\"comment-box-top\")&&function e(){let t=document.querySelector(\"#comment-add\");t&&t.parentNode.insertBefore(t,document.querySelector(\"#comments\"))}(),l(\"comment-cancel\")&&function e(){let t={childList:!0},l=(e,t)=>{for(let l of e){let n=l.target,o=n.querySelector(\"form.comment-add\");null!==o&&(c(o),s(n))}},n=new MutationObserver(l);document.querySelectorAll(\"blockquote.comment footer div.js-container\").forEach(e=>{n.observe(e,t)})}(),(l(\"nsfw-blur\")||l(\"nsfw-hide\"))&&document.querySelectorAll(\"article\").forEach(e=>{let t=e.querySelector(\".magazine-inline\");if(null!==e.querySelector(\"small.danger\")||d.includes(e.querySelector(\".entry__domain a\").innerText)||t&&d.includes(t.title.split(\"@\")[2])){if(l(\"nsfw-hide\"))e.remove();else{var n;(n=e).querySelectorAll(\"img\").forEach(e=>{e.classList.add(\"kes-blur\")}),n.querySelectorAll(\"figure img\").forEach(e=>{e.classList.add(\"kes-blur-large\")})}}}),l(\"hide-random\")&&function e(){let t=document.querySelector(\"#sidebar section.posts\");t&&(t.style=\"display:none;\");let l=document.querySelector(\"#sidebar section.entries\");l&&(l.style=\"display:none;\")}(),l(\"op-tag\")&&document.querySelectorAll(\"blockquote.author > header\").forEach(e=>{let t=document.createElement(\"small\");t.className=\"badge kbin-bg\",t.innerText=\"OP\",e.appendChild(t)}),\"1.9\"!=localStorage.getItem(\"setting-changelog-version\")){let u=`<strong>kbin enhancement script version: 1.9</strong><br>\n" +
    "\t\t\tThanks for downloading! You can always toggle on and off features in the kbin sidebar settings.<br>Recent changes:\n" +
    "\t\t\t<ul>\n" +
    "        <li>OP tag in comments</li>\n" +
    "        <li>Hide random sidebar</li>\n" +
    "\t\t\t\t<li>Fixed infinite scroll not showing domains</li>\n" +
    "        <li>Additional NSFW protection</li>\n" +
    "\t\t\t\t<li>Fixed markdown buttons and added \"Cancel\" when replying</li>\n" +
    "        <li>Bug Fixes</li>\n" +
    "\t\t\t</ul>\n" +
    "\t\t`,m=document.createElement(\"div\");m.id=\"kes-version-dialog\",m.innerHTML='<div class=\"kes-version-dialog-content\">'+u+\"<br><button>Close</button></div>\",document.body.appendChild(m),document.querySelector(\"#kes-version-dialog button\").addEventListener(\"click\",()=>{document.querySelector(\"#kes-version-dialog\").remove(),localStorage.setItem(\"setting-changelog-version\",\"1.9\")})}}();";

  /*https://greasyfork.org/en/scripts/468612-kbin-enhancement-script/code + https://greasyfork.org/en/scripts/468938-kbin-unsquash/code*/
  public static final String jsSource2 = "!function(){let e=document.createElement(\"style\");e.textContent=`\n" +
    "    .entry figure {overflow: hidden}\n" +
    "    .comment .badge {padding:.25rem;margin-left:5px}\n" +
    "    #kes-version-dialog {position: fixed; width: 100vw; height: 100vh; top: 0; left: 0; display: flex; align-items: center; justify-content: center; background-color: rgba(0,0,0,.3); z-index: 9999999}\n" +
    "    .kes-version-dialog-content {background: #ddd; color: #444; position: relative; padding: 40px}\n" +
    "\t\t.kes-expand {grid-gap: 0; padding-left:55px}\n" +
    "    .kes-blur {filter: blur(4px); transition-duration: 0.5s}\n" +
    "\t\t.kes-blur-large {filter: blur(15px)}\n" +
    "    .kes-blur:hover {filter: none}\n" +
    "  `,document.head.appendChild(e);let t=[{name:\"Show domains\",value:\"show-domains\"},{name:\"Show collapse comment\",value:\"show-collapse\"},{name:\"Show collapse replies\",value:\"show-collapse-replies\"},{name:\"Replies start collapsed\",value:\"start-collapse-replies\",default:\"false\"},{name:\"Move comment box to top\",value:\"comment-box-top\"},{name:\"Reply box improvements\",value:\"comment-cancel\"},{name:\"Hide known NSFW domains\",value:\"nsfw-hide\"},{name:\"Blur known NSFW domains\",value:\"nsfw-blur\"},{name:\"Hide random sidebar posts\",value:\"hide-random\"},{name:\"Add OP tag\",value:\"op-tag\"}];function l(e){let t=localStorage.getItem(\"setting-\"+e);return null===t&&(t=\"true\"),\"true\"===t}function n(e,t){localStorage.setItem(\"setting-\"+e,t),location.reload()}function o(e){let t=e.title.split(\"@\");if(t[2]!==location.hostname&&!e.innerText.includes(\"@\"+t[2])){let l=e.childNodes[e.childNodes.length-1];l.nodeValue+=\"@\"+t[2]}}function r(e,t){let l=e.id.split(\"-\")[2];t.push(e);let n=e.parentElement.querySelectorAll('blockquote[data-subject-parent-value=\"'+l+'\"]');n.forEach(e=>{r(e,t)})}function a(e){let t=[];!function e(t,l){let n=t.id.split(\"-\")[2];t.classList.contains(\"kes-expand\")&&l.push(t);let o=t.parentElement.querySelectorAll('blockquote[data-subject-parent-value=\"'+n+'\"]');o.forEach(t=>{e(t,l)})}(e,t),t.forEach(e=>{e.remove()})}function s(e){let t=e.id.split(\"-\")[2],l=\"1\";e.classList.forEach(e=>{e.includes(\"comment-level\")&&(l=e.split(\"--\")[1])});let n=[];r(e,n),n.forEach(e=>{e.style.display=\"none\"});let o=e.querySelector(\"header a\").innerText,s=e.querySelector(\"header time\").innerText,c=document.createElement(\"blockquote\");c.className=\"kes-expand section comment entry-comment comment-level--\"+l,c.dataset.subjectParentValue=t,c.innerHTML='<header><a href=\"javascript:;\">'+o+\", \"+s+\" [+]</a></header>\",c.querySelector(\"a\").addEventListener(\"click\",()=>{!function e(t){let l=[];r(t,l),l.forEach(e=>{e.style.display=\"\"}),a(t)}(e)}),e.parentNode.insertBefore(c,e)}function c(e){let t=e.querySelector(\"div.actions ul\"),l=document.createElement(\"li\");l.innerHTML='<div><button class=\"btn btn__primary\">Cancel</button></div>',t.appendChild(l),l.querySelector(\"button\").addEventListener(\"click\",()=>{var t;(t=e).innerHTML=\"\",t.style=\"\"})}function d(e){let t=e.action.split(\"/\"),l=\"entry_comment_body_\"+t[t.length-1];e.querySelector(\"#entry_comment_body\").id=l,e.querySelector(\"markdown-toolbar\").setAttribute(\"for\",l)}t.forEach(e=>{\"false\"===e.default&&null===localStorage.getItem(\"setting-\"+e.value)&&localStorage.setItem(\"setting-\"+e.value,\"false\")});let u=[\"lemmynsfw.com\",\"redgifs.com\"];if(!function e(){let o=document.querySelector(\".settings-list\"),r=document.createElement(\"strong\");r.textContent=\"kbin enhancement script\",o.appendChild(r),t.forEach(e=>{o.appendChild(function e(t,n){let o=l(n),r=document.createElement(\"div\");return r.className=\"row\",r.innerHTML=`<span>${t}:</span>\n" +
    "      <div>\n" +
    "        <a class=\"kes-setting-yes link-muted ${o?\"active\":\"\"}\" href=\"javascript:;\" data-setting=\"${n}\">\n" +
    "          Yes\n" +
    "        </a>\n" +
    "        |\n" +
    "        <a class=\"kes-setting-no link-muted ${o?\"\":\"active\"}\" href=\"javascript:;\" data-setting=\"${n}\">\n" +
    "          No\n" +
    "        </a>\n" +
    "      </div>`,r}(e.name,e.value))}),document.querySelectorAll(\".kes-setting-yes\").forEach(e=>{e.addEventListener(\"click\",()=>{n(e.dataset.setting,!0)})}),document.querySelectorAll(\".kes-setting-no\").forEach(e=>{e.addEventListener(\"click\",()=>{n(e.dataset.setting,!1)})})}(),l(\"show-domains\")&&function e(){document.querySelectorAll(\".magazine-inline, .user-inline\").forEach(e=>{o(e)});let t=(e,t)=>{for(let l of e)l.addedNodes.forEach(e=>{e.querySelectorAll(\".magazine-inline, .user-inline\").forEach(e=>{o(e)})})},l=new MutationObserver(t),n=document.querySelector(\"div#content > div\");n&&l.observe(n,{childList:!0})}(),l(\"show-collapse\")&&function e(){if(location.pathname.startsWith(\"/m\")){let t=document.querySelectorAll(\"blockquote.comment\");t.forEach(e=>{let t=e.querySelector(\"header\");if(!t.innerText.includes(\"[-]\")){let l=document.createElement(\"a\");l.href=\"javascript:;\",l.className=\"kes-collapse\",l.innerHTML=\"[-]\",t.appendChild(l)}}),document.querySelectorAll(\".kes-collapse\").forEach(e=>{e.addEventListener(\"click\",()=>{let t=e.closest(\"blockquote.comment\");s(t)})})}}(),l(\"show-collapse-replies\")&&function e(){if(location.pathname.startsWith(\"/m\")){let t=document.querySelectorAll(\"blockquote.comment-level--1\");t.forEach(e=>{let t=e.id.split(\"-\")[2],l=e.parentElement.querySelectorAll('blockquote[data-subject-parent-value=\"'+t+'\"]');if(l.length>0){let n=e.querySelector(\"footer menu\"),o=document.createElement(\"li\");o.innerHTML='<a href=\"javascript:;\" class=\"kes-collapse-replies\">toggle replies ('+function e(t){let l=[];return r(t,l),l.length-1}(e)+\")</a>\",n.appendChild(o)}}),document.querySelectorAll(\".kes-collapse-replies\").forEach(e=>{e.addEventListener(\"click\",()=>{let t=e.closest(\"blockquote.comment\");!function e(t,l){t.id.split(\"-\")[2];let n=function e(t){let l=[];return r(t,l),l.splice(l.indexOf(t),1),l}(t),o=!1;n.forEach(e=>{\"none\"==e.style.display&&(o=!0)}),n.forEach(e=>{e.style.display=o?\"\":\"none\"}),a(t)}(t)})})}}(),l(\"start-collapse-replies\")&&function e(){let t=document.querySelectorAll(\"blockquote.comment-level--2\");t.forEach(e=>{s(e)})}(),l(\"comment-box-top\")&&function e(){let t=document.querySelector(\"#comment-add\");t&&t.parentNode.insertBefore(t,document.querySelector(\"#comments\"))}(),l(\"comment-cancel\")&&function e(){let t={childList:!0},l=(e,t)=>{for(let l of e){let n=l.target,o=n.querySelector(\"form.comment-add\");null!==o&&(d(o),c(n))}},n=new MutationObserver(l);document.querySelectorAll(\"blockquote.comment footer div.js-container\").forEach(e=>{n.observe(e,t)})}(),(l(\"nsfw-blur\")||l(\"nsfw-hide\"))&&document.querySelectorAll(\"article\").forEach(e=>{let t=e.querySelector(\".magazine-inline\");if(null!==e.querySelector(\"small.danger\")||u.includes(e.querySelector(\".entry__domain a\").innerText)||t&&u.includes(t.title.split(\"@\")[2])){if(l(\"nsfw-hide\"))e.remove();else{var n;(n=e).querySelectorAll(\"img\").forEach(e=>{e.classList.add(\"kes-blur\")}),n.querySelectorAll(\"figure img\").forEach(e=>{e.classList.add(\"kes-blur-large\")})}}}),l(\"hide-random\")&&function e(){let t=document.querySelector(\"#sidebar section.posts\");t&&(t.style=\"display:none;\");let l=document.querySelector(\"#sidebar section.entries\");l&&(l.style=\"display:none;\")}(),l(\"op-tag\")&&document.querySelectorAll(\"blockquote.author > header\").forEach(e=>{let t=document.createElement(\"small\");t.className=\"badge kbin-bg\",t.innerText=\"OP\",e.appendChild(t)}),\"1.9\"!=localStorage.getItem(\"setting-changelog-version\")){let m=`<strong>kbin enhancement script version: 1.9</strong><br>\n" +
    "\t\t\tThanks for downloading! You can always toggle on and off features in the kbin sidebar settings.<br>Recent changes:\n" +
    "\t\t\t<ul>\n" +
    "        <li>OP tag in comments</li>\n" +
    "        <li>Hide random sidebar</li>\n" +
    "\t\t\t\t<li>Fixed infinite scroll not showing domains</li>\n" +
    "        <li>Additional NSFW protection</li>\n" +
    "\t\t\t\t<li>Fixed markdown buttons and added \"Cancel\" when replying</li>\n" +
    "        <li>Bug Fixes</li>\n" +
    "\t\t\t</ul>\n" +
    "\t\t`,f=document.createElement(\"div\");f.id=\"kes-version-dialog\",f.innerHTML='<div class=\"kes-version-dialog-content\">'+m+\"<br><button>Close</button></div>\",document.body.appendChild(f),document.querySelector(\"#kes-version-dialog button\").addEventListener(\"click\",()=>{document.querySelector(\"#kes-version-dialog\").remove(),localStorage.setItem(\"setting-changelog-version\",\"1.9\")})}}();const inlineSelector=\"footer figure a.thumb img\",fixedSelector=\"article figure a img\";function updateImg(e,t){var l;if(\"fixed\"==t)l=\"object-fit: cover !important\";else{var n=e.parentElement.href;e.src=n,l=\"max-width: 50% !important\"}e.style.cssText+=l}function checkItems(e){var t;t=\"fixed\"==e?\"article figure a img\":\"footer figure a.thumb img\";let l=document.querySelectorAll(t);l.forEach(t=>{updateImg(t,e)})}const imgTypes=[\"inline\",\"fixed\"];for(let i=0;i<imgTypes.length;i++)checkItems(imgTypes[i]);";

  public static final String cssSource = "header menu { overflow-x: scroll; width: 200px }";
}

/**
 * Quest alert (EN stub) — prefer scripts-zh-CN overlay when language is zh-CN.
 */
var status = -1;
function start() { status = -1; action(1, 0, 0); }
function action(mode, type, selection) {
    if (mode !== 1) { cm.dispose(); return; }
    status++;
    if (status === 0) {
        cm.sendOk("Quest alerts are available via the side toolbar. Switch to zh-CN scripts for the full list.");
    }
    cm.dispose();
}

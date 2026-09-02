
function action(mode, type, selection) {
    if (mode == 1) {
			im.teachSkill(5121003,20);
            im.getPlayer().changeKeybinding(30,1,5121003);
    }
    im.dispose();
}

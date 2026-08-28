var status = -1;
function action(h, e, d) {
    var c = ms.getEventManager("��������ѧԺ���1");
    var b = c.getInstance("��������ѧԺ���1");
    var g = c.getMapFactoryMap(ms.getMapId());
    for (var a = 0; a < 20; a++) {
        var f = c.getMonster(3501006);
        b.registerMonster(f);
        g.spawnMonsterOnGroundBelow(f, new java.awt.Point(ms.rand(-800, 400), 180))
    }
    ms.sendOk("������������Щ�����ŵ���һ�������Ҷ���\r\n\r\n#b���������й�����ٽ��жԻ��ɡ���", 1500017);
    ms.dispose()
}

function start() {
    status = -1;
    action(1, 0, 0)
};
export default {
  'clientWindowCashShopSync.page.desc':
    'Read the configured client Data root and sync New Cash Shop categories/items into the server DB. Does not modify client files; icon fill lives on Game Asset Hub, category CRUD on New Cash Shop Data.',
  'clientWindowCashShopSync.section.path': 'Shared client path',
  'clientWindowCashShopSync.section.ops': 'Client file read operations',
  'clientWindowCashShopSync.path.hint':
    'Uses the global ClientDataPath (same as Client Path). Save a valid …\\Data root before syncing.',
  'clientWindowCashShopSync.ops.hint':
    'Admin ops against client Data are read-oriented today: scan category/item layout and upsert into the server. Asset checks only probe whether client files exist. Use Game Asset Hub for icons.',
  'clientWindowCashShopSync.link.serverAdmin':
    'Open New Cash Shop Data (server)',
  'clientWindowCashShopSync.link.assetHub': 'Open Game Asset Hub',
};

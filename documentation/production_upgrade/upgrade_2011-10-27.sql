-- --------------------------------------------------------------------------------------
-- ======================================  DELETE  ======================================
-- --------------------------------------------------------------------------------------

-- ------------------------------  Role Permissions  -------------------------------
-- Open Flame

delete from role_permission
where id_role = (select id from opf_role where lookup = 'net.firejack.platform.admin') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-explanation.create');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'net.firejack.platform.admin') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-explanation.update');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'net.firejack.platform.admin') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-explanation.delete');

-- admin-console

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'com.millennialmedia.admin-console.console.advertiser');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'com.millennialmedia.admin-console.console.billing');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'com.millennialmedia.admin-console.console.reconciliation');

-- advertiser

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.directory.user.search');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.site.navigation-element.read-all-by-lookup');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.process.read');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.process.create');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.process.update');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.process.delete');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.process.search');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.actor.read');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.actor.create');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.actor.update');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.actor.delete');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.actor.search');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.actor.check-user-is-actor');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.process.activity.read');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.process.activity.create');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.process.activity.update');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.process.activity.delete');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.process.status.read');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.process.status.create');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.process.status.update');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.process.status.delete');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case.read');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case.create');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case.update');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case.delete');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case.search');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case.start-external');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case.perform');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case.rollback');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case.assign-team');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case.read-team');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case.read-next-team-member');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case.read-previous-team-member');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case.find');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.task.read');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.task.create');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.task.update');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.task.delete');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.task.search');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.task.assign');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.task.perform');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.task.rollback');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.task.read-next-team-member');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.task.read-previous-team-member');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-object.read');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-object.create');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-object.update');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-object.delete');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-note.read');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-note.create');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-note.update');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-note.delete');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-attachment.read-all-by-case');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-attachment.upload');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-attachment.update');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-attachment.delete');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-attachment.download');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-explanation.read');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-explanation.create');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-explanation.update');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-explanation.delete');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-explanation.search');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-action.read');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-action.create');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-action.update');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-action.delete');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'net.firejack.platform.process.case-action.read-all-by-case');

-- finance

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'com.millennialmedia.finance.customer-model.read-all-children');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'com.millennialmedia.finance.customer-model.read-customer');

-- sales

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'com.millennialmedia.sales.sales-force-model-vo.read');

delete from role_permission
where id_role = (select id from opf_role where lookup = 'com.millennialmedia.admin-console.millennial') and
      id_permission = (select id from opf_permission where lookup = 'com.millennialmedia.sales.sales-force-model-vo.read-all');



-- ---------------------------------  Tasks  ----------------------------------
-- advertiser

delete from opf_task
where id_activity in (
    select id from opf_activity
	where lookup = 'com.millennialmedia.advertiser.advertiser.campaign-launch-process.end' or
          lookup = 'com.millennialmedia.advertiser.advertiser.campaign-update-process.end'
);

-- -------------------------------  Activity  ---------------------------------
-- advertiser

delete from opf_activity
where lookup = 'com.millennialmedia.advertiser.advertiser.campaign-launch-process.end' or
      lookup = 'com.millennialmedia.advertiser.advertiser.campaign-update-process.end';


-- ------------------------------  Actor Role  --------------------------------
-- advertiser

delete from actor_role where id_actor in (select id from opf_registry_node where lookup = 'com.millennialmedia.advertiser.advertiser.system');


-- ---------------------------------  Actor  ----------------------------------
-- advertiser

delete from opf_registry_node where lookup = 'com.millennialmedia.advertiser.advertiser.system';

-- --------------------------------  Statuss  ---------------------------------
-- advertiser

delete from opf_status where lookup = 'com.millennialmedia.advertiser.advertiser.campaign-launch-process.closed';

delete from opf_status where lookup = 'com.millennialmedia.advertiser.advertiser.campaign-update-process.closed';

delete from opf_status where lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.launched';

-- --------------------------------  Permissions  ----------------------------------
-- Open Flame

delete from opf_permission where lookup = 'net.firejack.platform.process.case-explanation.create';

delete from opf_permission where lookup = 'net.firejack.platform.process.case-explanation.update';

delete from opf_permission where lookup = 'net.firejack.platform.process.case-explanation.delete';


-- -----------------------------  Resource Versions  -------------------------------
-- Open Flame

delete from opf_resource_version where id_resource in (
      select id from opf_registry_node where path = 'net.firejack.platform.process.case-explanation.create' and (type = 'TXT' or type = 'IMG' or type = 'HTM' or type = 'AUD' or type = 'VID')
);

delete from opf_resource_version where id_resource in (
      select id from opf_registry_node where path = 'net.firejack.platform.process.case-explanation.update' and (type = 'TXT' or type = 'IMG' or type = 'HTM' or type = 'AUD' or type = 'VID')
);

delete from opf_resource_version where id_resource in (
      select id from opf_registry_node where path = 'net.firejack.platform.process.case-explanation.delete' and (type = 'TXT' or type = 'IMG' or type = 'HTM' or type = 'AUD' or type = 'VID')
);

-- ---------------------------------  Resources  -----------------------------------
-- Open Flame

delete from opf_registry_node where path = 'net.firejack.platform.process.case-explanation.create' and (type = 'TXT' or type = 'IMG' or type = 'HTM' or type = 'AUD' or type = 'VID');

delete from opf_registry_node where path = 'net.firejack.platform.process.case-explanation.update' and (type = 'TXT' or type = 'IMG' or type = 'HTM' or type = 'AUD' or type = 'VID');

delete from opf_registry_node where path = 'net.firejack.platform.process.case-explanation.delete' and (type = 'TXT' or type = 'IMG' or type = 'HTM' or type = 'AUD' or type = 'VID');

-- ----------------------------------  Actions  ------------------------------------
-- Open Flame

delete from opf_registry_node where lookup = 'net.firejack.platform.process.case-explanation.create';

delete from opf_registry_node where lookup = 'net.firejack.platform.process.case-explanation.update';

delete from opf_registry_node where lookup = 'net.firejack.platform.process.case-explanation.delete';


-- --------------------------------------------------------------------------------------
-- ======================================  UPDATE  ======================================
-- --------------------------------------------------------------------------------------

-- -------------------------------  Relationships  ---------------------------------
-- Open Flame

update opf_registry_node set required = false where lookup = 'net.firejack.platform.process.case-action.case-action-user';

-- --------------------------------  Statuss  ---------------------------------
-- advertiser

update opf_status set order_position = 3 where lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.awaiting-delivery-approval';

update opf_status set order_position = 5 where lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.awaiting-finance-approval';

update opf_status set order_position = 7 where lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.approved';

-- -------------------------------  Activity  ---------------------------------
-- advertiser

update opf_activity set type = 1 where lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.account-manager-review';

update opf_activity set type = 1, order_position = 3 where lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.delivery-manager-review';

update opf_activity set type = 1, order_position = 5 where lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.finance-review';

update opf_activity set order_position = 7 where lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.system-export';

-- ------------------------------  Navigations  -------------------------------
-- admin-console

update opf_registry_node set sort_position = 13 where lookup = 'com.millennialmedia.admin-console.console.home';

update opf_registry_node set sort_position = 14 where lookup = 'com.millennialmedia.admin-console.console.advertiser';

update opf_registry_node set sort_position = 15 where lookup = 'com.millennialmedia.admin-console.console.publisher';

update opf_registry_node set sort_position = 17 where lookup = 'com.millennialmedia.admin-console.console.invoice';

update opf_registry_node set sort_position = 18 where lookup = 'com.millennialmedia.admin-console.console.reconciliation';

update opf_registry_node set sort_position = 19 where lookup = 'com.millennialmedia.admin-console.console.report';

update opf_registry_node set sort_position = 20 where lookup = 'com.millennialmedia.admin-console.console.report.advertiser-activity';

update opf_registry_node set sort_position = 21 where lookup = 'com.millennialmedia.admin-console.console.report.site';

update opf_registry_node set sort_position = 22 where lookup = 'com.millennialmedia.admin-console.console.marketplace';

update opf_registry_node set sort_position = 23 where lookup = 'com.millennialmedia.admin-console.console.member';

update opf_registry_node set sort_position = 24 where lookup = 'com.millennialmedia.admin-console.console.network';

-- -------------------------------   Actions   --------------------------------
-- Open Flame

update opf_registry_node set url_path = '/rest/registry/inbox/actor/search/', soap_method = 'searchAllActors' where lookup = 'net.firejack.platform.process.actor.search';

update opf_registry_node set soap_url_path = '/ws/registry/inbox/actor' where lookup = 'net.firejack.platform.process.actor.check-user-is-actor';

update opf_registry_node set url_path = '/rest/registry/inbox/case/assign_team' where lookup = 'net.firejack.platform.process.case.assign-team';

update opf_registry_node set url_path = '/rest/registry/inbox/case/read_team' where lookup = 'net.firejack.platform.process.case.read-team';

update opf_registry_node set url_path = '/rest/registry/inbox/case/read_next_team_member' where lookup = 'net.firejack.platform.process.case.read-next-team-member';

update opf_registry_node set url_path = '/rest/registry/inbox/case/read_previous_previous_member' where lookup = 'net.firejack.platform.process.case.read-previous-team-member';

update opf_registry_node set url_path = '/rest/registry/inbox/case/find' where lookup = 'net.firejack.platform.process.case.find';

update opf_registry_node set url_path = '/rest/registry/inbox/explanation/search/', soap_method = 'searchProcessExplanations' where lookup = 'net.firejack.platform.process.case-explanation.search';

update opf_registry_node set soap_url_path = '/ws/registry/inbox/action' where lookup = 'net.firejack.platform.process.case-action.read-all-by-case';

-- sales

update opf_registry_node
   set name = 'read-all-children',
       lookup = 'com.millennialmedia.sales.sales-force-model-vo.read-all-children',
	   soap_url_path = '/ws/sales-force'
where lookup = 'com.millennialmedia.sales.sales-force-model-vo.read-all';

-- ------------------------------  Permissions  -------------------------------
-- sales

update opf_permission
   set name = 'read-all-children',
       lookup = 'com.millennialmedia.sales.sales-force-model-vo.read-all-children'
where lookup = 'com.millennialmedia.sales.sales-force-model-vo.read-all';

-- -----------------------------  Resource Versions  -------------------------------
-- Open Flame

update opf_resource_version set text = 'Welcome to the Open Flame Platform home page.
                The Open Flame Platform allows you to manage any set of systems in the
                cloud through a single intuitive interface and construct truly coherent
                web services and applications schemes that compete with the best on the Internet.'
where id_resource = (select id from opf_registry_node where lookup = 'net.firejack.platform.site.welcome-message') and type = 'TXT';

update opf_resource_version set text = '&lt;html&gt;
&lt;body&gt;
&lt;h4&gt;Hello $task.assignee.username,&lt;/h4&gt;

&lt;p&gt;You have been assigned a task or a task has been assigned to your group.&lt;/p&gt;
&lt;p&gt;Details are provided below:&lt;/p&gt;

&lt;p&gt;Assignee: $task.assignee.username&lt;/p&gt;
&lt;p&gt;Process: $task.case.process.name&lt;/p&gt;
&lt;p&gt;Task: $task.description&lt;/p&gt;
&lt;p&gt;Status: $task.case.status.name&lt;/p&gt;
&lt;p&gt;Description: $task.description&lt;/p&gt;
&lt;p&gt;Process Started: $task.case.startDate&lt;/p&gt;
&lt;p&gt;Last Update: $task.updateDate&lt;/p&gt;
&lt;/body&gt;
&lt;/html&gt;'
where id_resource = (select id from opf_registry_node where lookup = 'net.firejack.platform.process.mail.task-assign-template') and type = 'TXT';

update opf_resource_version set text = '&lt;html&gt;
&lt;body&gt;
&lt;h4&gt;Hello $task.assignee.username,&lt;/h4&gt;

&lt;p&gt;You have been assigned a task or a task has been assigned to your group.&lt;/p&gt;
&lt;p&gt;Details are provided below:&lt;/p&gt;

&lt;p&gt;Assignee: $task.assignee.username&lt;/p&gt;
&lt;p&gt;Process: $task.case.process.name&lt;/p&gt;
&lt;p&gt;Task: $task.description&lt;/p&gt;
&lt;p&gt;Status: $task.case.status.name&lt;/p&gt;
&lt;p&gt;Description: $task.description&lt;/p&gt;
&lt;p&gt;Process Started: $task.case.startDate&lt;/p&gt;
&lt;p&gt;Last Update: $task.updateDate&lt;/p&gt;
&lt;/body&gt;
&lt;/html&gt;'
where id_resource = (select id from opf_registry_node where lookup = 'net.firejack.platform.process.mail.task-create-template') and type = 'TXT';




-- --------------------------------------------------------------------------------------
-- ======================================  INSERT  ======================================
-- --------------------------------------------------------------------------------------

-- ------------------------------  Navigations  -------------------------------
-- admin-console

insert into opf_registry_node(name, sort_position, lookup, path, url_path, type, id_parent)
     select
	     'home-alt',
		 12,
		 'com.millennialmedia.admin-console.console.home-alt',
		 'com.millennialmedia.admin-console.console',
		 '/',
		 'NAV',
		 parentNav.id
	 from opf_registry_node parentNav
	 where parentNav.lookup = 'com.millennialmedia.admin-console.console';


-- -------------------------------   Actions   --------------------------------
-- Open Flame


insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'read-my-processes',
	  'net.firejack.platform.process.process',
	  'net.firejack.platform.process.process.read-my-processes',
	  '/rest/registry/inbox/process/my',
	  0,
	  'ACT',
	  0,
	  'readMyProcesses',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.process';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_url_path, soap_method, id_parent)
   select
      'read-by-lookup',
	  'net.firejack.platform.process.actor',
	  'net.firejack.platform.process.actor.read-by-lookup',
	  '/rest/registry/inbox/actor/actor-by-lookup',
	  0,
	  'ACT',
	  0,
	  '/ws/registry/inbox/actor',
	  'getActorIdByLookup',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.actor';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_url_path, soap_method, id_parent)
   select
      'assign-user',
	  'net.firejack.platform.process.actor',
	  'net.firejack.platform.process.actor.assign-user',
	  '/rest/registry/inbox/actor/assign-user-to-actor',
	  0,
	  'ACT',
	  2,
	  '/ws/registry/inbox/actor',
	  'assignUserToActor',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.actor';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'read-by-process',
	  'net.firejack.platform.process.process.activity',
	  'net.firejack.platform.process.process.activity.read-by-process',
	  '/rest/registry/inbox/activity/process',
	  0,
	  'ACT',
	  0,
	  'readActivitiesByProcess',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.process.activity';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'read-by-process',
	  'net.firejack.platform.process.process.status',
	  'net.firejack.platform.process.process.status.read-by-process',
	  '/rest/registry/inbox/status/process',
	  0,
	  'ACT',
	  0,
	  'readStatusesByProcess',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.process.status';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'filter-all',
	  'net.firejack.platform.process.case',
	  'net.firejack.platform.process.case.filter-all',
	  '/rest/registry/inbox/case/search',
	  0,
	  'ACT',
	  1,
	  'filterAllCases',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'stop-external',
	  'net.firejack.platform.process.case',
	  'net.firejack.platform.process.case.stop-external',
	  '/rest/registry/inbox/case/external/stop',
	  0,
	  'ACT',
	  2,
	  'stopCaseExternal',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'read-next-assignee-candidates',
	  'net.firejack.platform.process.case',
	  'net.firejack.platform.process.case.read-next-assignee-candidates',
	  '/rest/registry/inbox/case/next-assignee-candidates',
	  0,
	  'ACT',
	  0,
	  'readNextAssigneeCandidates',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'read-previous-assignee-candidates',
	  'net.firejack.platform.process.case',
	  'net.firejack.platform.process.case.read-previous-assignee-candidates',
	  '/rest/registry/inbox/case/next-assignee-candidates',
	  0,
	  'ACT',
	  0,
	  'readPreviousAssigneeCandidates',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'find-statuses',
	  'net.firejack.platform.process.case',
	  'net.firejack.platform.process.case.find-statuses',
	  '/rest/registry/inbox/case/find-statuses',
	  0,
	  'ACT',
	  1,
	  'processEntityStatuses',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'find-through-actors',
	  'net.firejack.platform.process.case',
	  'net.firejack.platform.process.case.find-through-actors',
	  '/rest/registry/inbox/case/find-through-actors',
	  0,
	  'ACT',
	  0,
	  'findThroughActors',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'find-belonging-to-user-actor',
	  'net.firejack.platform.process.case',
	  'net.firejack.platform.process.case.find-belonging-to-user-actor',
	  '/rest/registry/inbox/case/find-belonging-to-user-actor',
	  0,
	  'ACT',
	  0,
	  'findBelongingToUserActor',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'find-closed-cases-for-current-user',
	  'net.firejack.platform.process.case',
	  'net.firejack.platform.process.case.find-closed-cases-for-current-user',
	  '/rest/registry/inbox/case/find-closed-cases-for-current-user',
	  0,
	  'ACT',
	  0,
	  'findBelongingToUserActor',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'find-active',
	  'net.firejack.platform.process.case',
	  'net.firejack.platform.process.case.find-active',
	  '/rest/registry/inbox/case/find-active',
	  0,
	  'ACT',
	  0,
	  'findActive',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'reset',
	  'net.firejack.platform.process.case',
	  'net.firejack.platform.process.case.reset',
	  '/rest/registry/inbox/case/reset',
	  0,
	  'ACT',
	  2,
	  'reset',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'read-next-assignee-candidates',
	  'net.firejack.platform.process.task',
	  'net.firejack.platform.process.task.read-next-assignee-candidates',
	  '/rest/registry/inbox/task/next-assignee-candidates',
	  0,
	  'ACT',
	  0,
	  'readNextAssigneeCandidates',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.task';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'read-assignee-candidates',
	  'net.firejack.platform.process.task',
	  'net.firejack.platform.process.task.read-assignee-candidates',
	  '/rest/registry/inbox/task/assignee-candidates',
	  0,
	  'ACT',
	  0,
	  'readAssigneeCandidates',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.task';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'read-previous-assignee-candidates',
	  'net.firejack.platform.process.task',
	  'net.firejack.platform.process.task.read-previous-assignee-candidates',
	  '/rest/registry/inbox/task/previous-assignee-candidates',
	  0,
	  'ACT',
	  0,
	  'readPreviousAssigneeCandidates',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.task';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'search-through-actors',
	  'net.firejack.platform.process.task',
	  'net.firejack.platform.process.task.search-through-actors',
	  '/rest/registry/inbox/task/find-through-actors',
	  0,
	  'ACT',
	  1,
	  'findThroughActors',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.task';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'filter-all',
	  'net.firejack.platform.process.task',
	  'net.firejack.platform.process.task.filter-all',
	  '/rest/registry/inbox/task/search',
	  0,
	  'ACT',
	  1,
	  'searchTask',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.task';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'find-closed-tasks-for-current-user',
	  'net.firejack.platform.process.task',
	  'net.firejack.platform.process.task.find-closed-tasks-for-current-user',
	  '/rest/registry/inbox/task/find-closed-tasks-for-current-user',
	  0,
	  'ACT',
	  0,
	  'findClosedTasksForCurrentUser',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.task';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'find-belonging-to-user-actor',
	  'net.firejack.platform.process.task',
	  'net.firejack.platform.process.task.find-belonging-to-user-actor',
	  '/rest/registry/inbox/task/find-belonging-to-user-actor',
	  0,
	  'ACT',
	  0,
	  'findBelongingToUserActor',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.task';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'search-by-assignee',
	  'net.firejack.platform.process.case-object',
	  'net.firejack.platform.process.case-object.search-by-assignee',
	  '/rest/registry/inbox/object/search-by-assignee',
	  0,
	  'ACT',
	  0,
	  'searchByAssignee',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case-object';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'read-by-task',
	  'net.firejack.platform.process.case-note',
	  'net.firejack.platform.process.case-note.read-by-task',
	  '/rest/registry/inbox/note/task',
	  0,
	  'ACT',
	  0,
	  'readAllCaseNotesByTask',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case-note';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'read-by-case',
	  'net.firejack.platform.process.case-note',
	  'net.firejack.platform.process.case-note.read-by-case',
	  '/rest/registry/inbox/note/case',
	  0,
	  'ACT',
	  0,
	  'readAllCaseNotesByCase',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case-note';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'read-by-case-object',
	  'net.firejack.platform.process.case-note',
	  'net.firejack.platform.process.case-note.read-by-case-object',
	  '/rest/registry/inbox/note/case-object/entity',
	  0,
	  'ACT',
	  0,
	  'readAllCaseNotesByCaseObject',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case-note';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'search-by-task',
	  'net.firejack.platform.process.case-note',
	  'net.firejack.platform.process.case-note.search-by-task',
	  '/rest/registry/inbox/note/search/task',
	  0,
	  'ACT',
	  0,
	  'searchCaseNoteByTask',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case-note';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_method, id_parent)
   select
      'search-by-case',
	  'net.firejack.platform.process.case-note',
	  'net.firejack.platform.process.case-note.search-by-case',
	  '/rest/registry/inbox/note/search/case',
	  0,
	  'ACT',
	  0,
	  'searchCaseNoteByCase',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case-note';

-- advertiser

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_url_path, soap_method, id_parent)
   select
      'map',
	  'com.millennialmedia.advertiser.client-vo',
	  'com.millennialmedia.advertiser.client-vo.map',
	  '/rest/client/account-reconciliation/map',
	  0,
	  'ACT',
	  2,
	  '/ws/client/account-reconciliation',
	  'mapData',
	  par.id
   from opf_registry_node par
   where par.lookup = 'com.millennialmedia.advertiser.client-vo';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_url_path, soap_method, id_parent)
   select
      'unmap',
	  'com.millennialmedia.advertiser.client-vo',
	  'com.millennialmedia.advertiser.client-vo.unmap',
	  '/rest/client/account-reconciliation/unmap',
	  0,
	  'ACT',
	  2,
	  '/ws/client/account-reconciliation',
	  'unMapData',
	  par.id
   from opf_registry_node par
   where par.lookup = 'com.millennialmedia.advertiser.client-vo';

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_url_path, soap_method, id_parent)
   select
      'read',
	  'com.millennialmedia.advertiser.invoice.invoice-line-association',
	  'com.millennialmedia.advertiser.invoice.invoice-line-association.read',
	  '/rest/finance/invoice/line/association',
	  0,
	  'ACT',
	  0,
	  '/ws/advertiser/finance/invoice/line/association',
	  'readInvoiceLineAssociation',
	  par.id
   from opf_registry_node par
   where par.lookup = 'com.millennialmedia.advertiser.invoice.invoice-line-association';

-- sales

insert into opf_registry_node (name, path, lookup, url_path, protocol, type, method, soap_url_path, soap_method, id_parent)
   select
      'read-all-related',
	  'com.millennialmedia.sales.sales-force-model-vo',
	  'com.millennialmedia.sales.sales-force-model-vo.read-all-related',
	  '/rest/sales-force/children/related',
	  0,
	  'ACT',
	  0,
	  '/ws/sales-force',
	  'readRelatedByClientEntity',
	  par.id
   from opf_registry_node par
   where par.lookup = 'com.millennialmedia.sales.sales-force-model-vo';

-- -----------------------   Action Parameters   ------------------------------
-- Open Flame

insert into opf_action_parameter(name, type, location, order_position, id_action)
   select
      'actorLookup',
	  3,
	  0,
	  1,
	  act.id
   from opf_registry_node act
   where act.lookup = 'net.firejack.platform.process.actor.read-by-lookup';

insert into opf_action_parameter(name, type, location, order_position, id_action)
   select
      'actorLookup',
	  3,
	  1,
	  1,
	  act.id
   from opf_registry_node act
   where act.lookup = 'net.firejack.platform.process.actor.assign-user';

insert into opf_action_parameter(name, type, location, order_position, id_action)
   select
      'userId',
	  12,
	  1,
	  2,
	  act.id
   from opf_registry_node act
   where act.lookup = 'net.firejack.platform.process.actor.assign-user';

insert into opf_action_parameter(name, type, location, order_position, id_action)
   select
      'caseId',
	  12,
	  1,
	  3,
	  act.id
   from opf_registry_node act
   where act.lookup = 'net.firejack.platform.process.actor.assign-user';

insert into opf_action_parameter(name, type, location, order_position, id_action)
   select
      'processId',
	  12,
	  0,
	  1,
	  act.id
   from opf_registry_node act
   where act.lookup = 'net.firejack.platform.process.process.status.read-by-process';

insert into opf_action_parameter(name, type, location, order_position, id_action)
   select
      'entityType',
	  3,
	  1,
	  1,
	  act.id
   from opf_registry_node act
   where act.lookup = 'net.firejack.platform.process.case-object.search-by-assignee';

insert into opf_action_parameter(name, type, location, order_position, id_action)
   select
      'assigneeId',
	  12,
	  1,
	  2,
	  act.id
   from opf_registry_node act
   where act.lookup = 'net.firejack.platform.process.case-object.search-by-assignee';

insert into opf_action_parameter(name, type, location, order_position, id_action)
   select
      'taskId',
	  12,
	  1,
	  1,
	  act.id
   from opf_registry_node act
   where act.lookup = 'net.firejack.platform.process.case-note.read-by-task';

insert into opf_action_parameter(name, type, location, order_position, id_action)
   select
      'caseId',
	  12,
	  1,
	  1,
	  act.id
   from opf_registry_node act
   where act.lookup = 'net.firejack.platform.process.case-note.read-by-case';

insert into opf_action_parameter(name, type, location, order_position, id_action)
   select
      'entityType',
	  3,
	  0,
	  1,
	  act.id
   from opf_registry_node act
   where act.lookup = 'net.firejack.platform.process.case-note.read-by-case-object';

insert into opf_action_parameter(name, type, location, order_position, id_action)
   select
      'entityId',
	  12,
	  0,
	  2,
	  act.id
   from opf_registry_node act
   where act.lookup = 'net.firejack.platform.process.case-note.read-by-case-object';

insert into opf_action_parameter(name, type, location, order_position, id_action)
   select
      'term',
	  3,
	  0,
	  1,
	  act.id
   from opf_registry_node act
   where act.lookup = 'net.firejack.platform.process.case-note.search-by-task';

insert into opf_action_parameter(name, type, location, order_position, id_action)
   select
      'taskId',
	  12,
	  0,
	  2,
	  act.id
   from opf_registry_node act
   where act.lookup = 'net.firejack.platform.process.case-note.search-by-task';

insert into opf_action_parameter(name, type, location, order_position, id_action)
   select
      'term',
	  3,
	  0,
	  1,
	  act.id
   from opf_registry_node act
   where act.lookup = 'net.firejack.platform.process.case-note.search-by-case';

insert into opf_action_parameter(name, type, location, order_position, id_action)
   select
      'caseId',
	  12,
	  0,
	  2,
	  act.id
   from opf_registry_node act
   where act.lookup = 'net.firejack.platform.process.case-note.search-by-case';

insert into opf_action_parameter(name, type, location, order_position, id_action)
   select
      'processId',
	  12,
	  1,
	  2,
	  act.id
   from opf_registry_node act
   where act.lookup = 'net.firejack.platform.process.case-explanation.read';

insert into opf_action_parameter(name, type, location, order_position, id_action)
   select
      'term',
	  3,
	  1,
	  1,
	  act.id
   from opf_registry_node act
   where act.lookup = 'net.firejack.platform.process.case-explanation.read';


-- advertiser

insert into opf_action_parameter(name, type, location, order_position, id_action)
   select
      'invoiceLineAssociationId',
	  12,
	  0,
	  1,
	  act.id
   from opf_registry_node act
   where act.lookup = 'com.millennialmedia.advertiser.invoice.invoice-line-association.read';

-- sales

insert into opf_action_parameter(name, type, location, order_position, id_action)
   select
      'parentId',
	  12,
	  0,
	  1,
	  act.id
   from opf_registry_node act
   where act.lookup = 'com.millennialmedia.sales.sales-force-model-vo.read-all-related';

-- ---------------------------  Permissions  -----------------------------
-- Open Flame

insert into opf_permission (name, lookup, path, id_parent)
   select
      'read-my-processes',
	  'net.firejack.platform.process.process.read-my-processes',
	  'net.firejack.platform.process.process',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.process';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'read-by-lookup',
	  'net.firejack.platform.process.actor.read-by-lookup',
	  'net.firejack.platform.process.actor',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.actor';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'assign-user',
	  'net.firejack.platform.process.actor.assign-user',
	  'net.firejack.platform.process.actor',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.actor';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'read-by-process',
	  'net.firejack.platform.process.process.activity.read-by-process',
	  'net.firejack.platform.process.process.activity',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.process.activity';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'read-by-process',
	  'net.firejack.platform.process.process.status.read-by-process',
	  'net.firejack.platform.process.process.status',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.process.status';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'filter-all',
	  'net.firejack.platform.process.case.filter-all',
	  'net.firejack.platform.process.case',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'read-next-assignee-candidates',
	  'net.firejack.platform.process.case.read-next-assignee-candidates',
	  'net.firejack.platform.process.case',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'read-previous-assignee-candidates',
	  'net.firejack.platform.process.case.read-previous-assignee-candidates',
	  'net.firejack.platform.process.case',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'find-statuses',
	  'net.firejack.platform.process.case.find-statuses',
	  'net.firejack.platform.process.case',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'find-through-actors',
	  'net.firejack.platform.process.case.find-through-actors',
	  'net.firejack.platform.process.case',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'stop-external',
	  'net.firejack.platform.process.case.stop-external',
	  'net.firejack.platform.process.case',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'find-belonging-to-user-actor',
	  'net.firejack.platform.process.case.find-belonging-to-user-actor',
	  'net.firejack.platform.process.case',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'find-closed-cases-for-current-user',
	  'net.firejack.platform.process.case.find-closed-cases-for-current-user',
	  'net.firejack.platform.process.case',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'find-active',
	  'net.firejack.platform.process.case.find-active',
	  'net.firejack.platform.process.case',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'reset',
	  'net.firejack.platform.process.case.reset',
	  'net.firejack.platform.process.case',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'reset',
	  'net.firejack.platform.process.case.reset',
	  'net.firejack.platform.process.case',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'read-next-assignee-candidates',
	  'net.firejack.platform.process.task.read-next-assignee-candidates',
	  'net.firejack.platform.process.task',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.task';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'read-assignee-candidates',
	  'net.firejack.platform.process.task.read-assignee-candidates',
	  'net.firejack.platform.process.task',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.task';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'read-previous-assignee-candidates',
	  'net.firejack.platform.process.task.read-previous-assignee-candidates',
	  'net.firejack.platform.process.task',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.task';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'filter-all',
	  'net.firejack.platform.process.task.filter-all',
	  'net.firejack.platform.process.task',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.task';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'search-through-actors',
	  'net.firejack.platform.process.task.search-through-actors',
	  'net.firejack.platform.process.task',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.task';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'find-closed-tasks-for-current-user',
	  'net.firejack.platform.process.task.find-closed-tasks-for-current-user',
	  'net.firejack.platform.process.task',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.task';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'find-belonging-to-user-actor',
	  'net.firejack.platform.process.task.find-belonging-to-user-actor',
	  'net.firejack.platform.process.task',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.task';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'search-by-assignee',
	  'net.firejack.platform.process.case-object.search-by-assignee',
	  'net.firejack.platform.process.case-object',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case-object';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'read-by-task',
	  'net.firejack.platform.process.case-note.read-by-task',
	  'net.firejack.platform.process.case-note',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case-note';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'read-by-case',
	  'net.firejack.platform.process.case-note.read-by-case',
	  'net.firejack.platform.process.case-note',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case-note';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'read-by-case-object',
	  'net.firejack.platform.process.case-note.read-by-case-object',
	  'net.firejack.platform.process.case-note',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case-note';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'search-by-task',
	  'net.firejack.platform.process.case-note.search-by-task',
	  'net.firejack.platform.process.case-note',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case-note';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'search-by-case',
	  'net.firejack.platform.process.case-note.search-by-case',
	  'net.firejack.platform.process.case-note',
	  par.id
   from opf_registry_node par
   where par.lookup = 'net.firejack.platform.process.case-note';

-- admin-console

insert into opf_permission (name, lookup, path, id_parent)
   select
      'home-alt',
	  'com.millennialmedia.admin-console.console.home-alt',
	  'com.millennialmedia.admin-console.console',
	  par.id
   from opf_registry_node par
   where par.lookup = 'com.millennialmedia.admin-console.console';

-- advertiser

insert into opf_permission (name, lookup, path, id_parent)
   select
      'map',
	  'com.millennialmedia.advertiser.client-vo.map',
	  'com.millennialmedia.advertiser.client-vo',
	  par.id
   from opf_registry_node par
   where par.lookup = 'com.millennialmedia.advertiser.client-vo';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'unmap',
	  'com.millennialmedia.advertiser.client-vo.unmap',
	  'com.millennialmedia.advertiser.client-vo',
	  par.id
   from opf_registry_node par
   where par.lookup = 'com.millennialmedia.advertiser.client-vo';

insert into opf_permission (name, lookup, path, id_parent)
   select
      'read',
	  'com.millennialmedia.advertiser.invoice.invoice-line-association.read',
	  'com.millennialmedia.advertiser.invoice.invoice-line-association',
	  par.id
   from opf_registry_node par
   where par.lookup = 'com.millennialmedia.advertiser.invoice.invoice-line-association';

-- sales

insert into opf_permission (name, lookup, path, id_parent)
   select
      'read-all-related',
	  'com.millennialmedia.sales.sales-force-model-vo.read-all-related',
	  'com.millennialmedia.sales.sales-force-model-vo',
	  par.id
   from opf_registry_node par
   where par.lookup = 'com.millennialmedia.sales.sales-force-model-vo';

-- -------------------------------  Roles  -------------------------------
-- admin-console

insert into opf_role (name, lookup, path, id_parent)
   select
       'reconciliation-manager',
	   'com.millennialmedia.admin-console.reconciliation-manager',
	   'com.millennialmedia.admin-console',
	   par.id
   from opf_registry_node par
   where par.lookup = 'com.millennialmedia.admin-console';

-- ---------------------------  Role Permissions  --------------------------
-- Open Flame

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.process.read-my-processes';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.actor.read-by-lookup';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.actor.assign-user';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.process.activity.read-by-process';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.process.status.read-by-process';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.case.stop-external';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.case.filter-all';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.case.read-next-assignee-candidates';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.case.read-previous-assignee-candidates';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.case.find-statuses';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.case.find-through-actors';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.case.find-belonging-to-user-actor';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.case.find-closed-cases-for-current-user';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.case.find-active';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.case.reset';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.task.read-next-assignee-candidates';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.task.read-assignee-candidates';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.task.read-previous-assignee-candidates';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.task.filter-all';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.task.search-through-actors';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.task.find-belonging-to-user-actor';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.task.find-closed-tasks-for-current-user';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.case-object.search-by-assignee';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.case-note.read-by-task';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.case-note.read-by-case';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.case-note.read-by-case-object';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.case-note.search-by-task';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.admin' and
         p.lookup = 'net.firejack.platform.process.case-note.search-by-case';


-- admin-console

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.reconciliation-manager' and
         p.lookup = 'com.millennialmedia.admin-console.console.reconciliation';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'com.millennialmedia.admin-console.console.home-alt';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.directory.user.search';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.site.navigation-element.read-all-by-lookup';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.process.read';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.process.read-my-processes';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.process.create';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.process.update';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.process.delete';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.process.search';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.actor.read';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.actor.create';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.actor.update';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.actor.delete';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.actor.search';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.actor.read-by-lookup';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.actor.assign-user';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.actor.check-user-is-actor';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.process.activity.read';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.process.activity.read-by-process';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.process.activity.create';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.process.activity.update';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.process.activity.delete';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.process.status.read';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.process.status.read-by-process';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.process.status.create';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.process.status.update';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.process.status.delete';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.case-explanation.search';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.case-action.read';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.case-action.update';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.case-action.delete';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.case-action.read-all-by-case';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.registry.registry-node.action.read-from-cache';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.authority.permission.read-map-by-roles';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.task.find-belonging-to-user-actor';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.task.find-closed-tasks-for-current-user';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.case.find-belonging-to-user-actor';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.process.case.find-closed-cases-for-current-user';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'net.firejack.platform.authority.resource-location.read-masked-resources';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'net.firejack.platform.guest' and
         p.lookup = 'com.millennialmedia.admin-console.console.home-alt';


-- advertiser

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.reconciliation-manager' and
         p.lookup = 'com.millennialmedia.advertiser.client-vo.map';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.reconciliation-manager' and
         p.lookup = 'com.millennialmedia.advertiser.client-vo.unmap';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.millennial' and
         p.lookup = 'com.millennialmedia.advertiser.invoice.invoice-line-association.read';


-- finance

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.reconciliation-manager' and
         p.lookup = 'com.millennialmedia.finance.customer-model.read-all-children';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.reconciliation-manager' and
         p.lookup = 'com.millennialmedia.finance.customer-model.read-customer';


-- sales

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.reconciliation-manager' and
         p.lookup = 'com.millennialmedia.sales.sales-force-model-vo.read';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.reconciliation-manager' and
         p.lookup = 'com.millennialmedia.sales.sales-force-model-vo.read-all-children';

insert into role_permission (id_role, id_permission)
   select
      r.id, p.id
   from opf_role r, opf_permission p
   where r.lookup = 'com.millennialmedia.admin-console.reconciliation-manager' and
         p.lookup = 'com.millennialmedia.sales.sales-force-model-vo.read-all-related';

-- ------------------------------  Statuses  -------------------------------
-- advertiser

insert into opf_status (name, path, lookup, order_position, id_process)
    select
	   'AM ready',
	   'com.millennialmedia.advertiser.invoice.invoice-approval-process',
	   'com.millennialmedia.advertiser.invoice.invoice-approval-process.am-ready',
	   2,
	   pr.id
	from opf_registry_node pr
	where pr.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process';

insert into opf_status (name, path, lookup, order_position, id_process)
    select
	   'DM ready',
	   'com.millennialmedia.advertiser.invoice.invoice-approval-process',
	   'com.millennialmedia.advertiser.invoice.invoice-approval-process.dm-ready',
	   4,
	   pr.id
	from opf_registry_node pr
	where pr.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process';

insert into opf_status (name, path, lookup, order_position, id_process)
    select
	   'finance ready',
	   'com.millennialmedia.advertiser.invoice.invoice-approval-process',
	   'com.millennialmedia.advertiser.invoice.invoice-approval-process.finance-ready',
	   6,
	   pr.id
	from opf_registry_node pr
	where pr.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process';

-- ------------------------------   Activity   -----------------------------
-- advertiser

insert into opf_activity (type, name, path, lookup, order_position, id_process, id_actor, id_status)
     select
     	 1,
		 'Account Manager Ready',
		 'com.millennialmedia.advertiser.invoice.invoice-approval-process',
		 'com.millennialmedia.advertiser.invoice.invoice-approval-process.account-manager-ready',
		 2,
		 pr.id,
		 ac.id,
		 st.id
     from opf_registry_node ac, opf_status st
	     inner join opf_registry_node pr on pr.id = st.id_process
     where st.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.am-ready' and
	       pr.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process' and
	       ac.lookup = 'com.millennialmedia.advertiser.advertiser.account-manager';

insert into opf_activity (type, name, path, lookup, order_position, id_process, id_actor, id_status)
     select
     	 1,
		 'Delivery Manager Ready',
		 'com.millennialmedia.advertiser.invoice.invoice-approval-process',
		 'com.millennialmedia.advertiser.invoice.invoice-approval-process.delivery-manager-ready',
		 4,
		 pr.id,
		 ac.id,
		 st.id
     from opf_registry_node ac, opf_status st
	     inner join opf_registry_node pr on pr.id = st.id_process
     where st.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.dm-ready' and
	       pr.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process' and
	       ac.lookup = 'com.millennialmedia.advertiser.advertiser.delivery-manager';

insert into opf_activity (type, name, path, lookup, order_position, id_process, id_actor, id_status)
     select
     	 1,
		 'Finance Ready',
		 'com.millennialmedia.advertiser.invoice.invoice-approval-process',
		 'com.millennialmedia.advertiser.invoice.invoice-approval-process.finance-ready',
		 6,
		 pr.id,
		 ac.id,
		 st.id
     from opf_registry_node ac, opf_status st
	     inner join opf_registry_node pr on pr.id = st.id_process
     where st.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.finance-ready' and
	       pr.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process' and
	       ac.lookup = 'com.millennialmedia.advertiser.invoice.finance';

-- --------------------------------  Resources  --------------------------------------
-- Open Flame

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.process.read-my-processes.rest-example',
		'net.firejack.platform.process.process.read-my-processes',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.process.read-my-processes';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.process.read-my-processes.soap-example',
		'net.firejack.platform.process.process.read-my-processes',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.process.read-my-processes';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.process.read-my-processes.blazeds-example',
		'net.firejack.platform.process.process.read-my-processes',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.process.read-my-processes';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.actor.read-by-lookup.rest-example',
		'net.firejack.platform.process.actor.read-by-lookup',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.actor.read-by-lookup';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.actor.read-by-lookup.soap-example',
		'net.firejack.platform.process.actor.read-by-lookup',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.actor.read-by-lookup';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.actor.read-by-lookup.blazeds-example',
		'net.firejack.platform.process.actor.read-by-lookup',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.actor.read-by-lookup';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.actor.assign-user.rest-example',
		'net.firejack.platform.process.actor.assign-user',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.actor.assign-user';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.actor.assign-user.soap-example',
		'net.firejack.platform.process.actor.assign-user',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.actor.assign-user';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.actor.assign-user.blazeds-example',
		'net.firejack.platform.process.actor.assign-user',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.actor.assign-user';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.process.activity.read-by-process.rest-example',
		'net.firejack.platform.process.process.activity.read-by-process',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.process.activity.read-by-process';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.process.activity.read-by-process.soap-example',
		'net.firejack.platform.process.process.activity.read-by-process',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.process.activity.read-by-process';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.process.activity.read-by-process.blazeds-example',
		'net.firejack.platform.process.process.activity.read-by-process',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.process.activity.read-by-process';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.process.status.read-by-process.rest-example',
		'net.firejack.platform.process.process.status.read-by-process',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.process.status.read-by-process';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.process.status.read-by-process.soap-example',
		'net.firejack.platform.process.process.status.read-by-process',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.process.status.read-by-process';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.process.status.read-by-process.blazeds-example',
		'net.firejack.platform.process.process.status.read-by-process',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.process.status.read-by-process';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.case.filter-all.rest-example',
		'net.firejack.platform.process.case.filter-all',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.filter-all';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.case.filter-all.soap-example',
		'net.firejack.platform.process.case.filter-all',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.filter-all';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.case.filter-all.blazeds-example',
		'net.firejack.platform.process.case.filter-all',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.filter-all';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.case.stop-external.rest-example',
		'net.firejack.platform.process.case.stop-external',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.stop-external';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.case.stop-external.soap-example',
		'net.firejack.platform.process.case.stop-external',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.stop-external';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.case.stop-external.blazeds-example',
		'net.firejack.platform.process.case.stop-external',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.stop-external';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.case.read-next-assignee-candidates.rest-example',
		'net.firejack.platform.process.case.read-next-assignee-candidates',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.read-next-assignee-candidates';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.case.read-next-assignee-candidates.soap-example',
		'net.firejack.platform.process.case.read-next-assignee-candidates',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.read-next-assignee-candidates';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.case.read-next-assignee-candidates.blazeds-example',
		'net.firejack.platform.process.case.read-next-assignee-candidates',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.read-next-assignee-candidates';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.case.read-previous-assignee-candidates.rest-example',
		'net.firejack.platform.process.case.read-previous-assignee-candidates',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.read-previous-assignee-candidates';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.case.read-previous-assignee-candidates.soap-example',
		'net.firejack.platform.process.case.read-previous-assignee-candidates',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.read-previous-assignee-candidates';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.case.read-previous-assignee-candidates.blazeds-example',
		'net.firejack.platform.process.case.read-previous-assignee-candidates',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.read-previous-assignee-candidates';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.case.find-statuses.rest-example',
		'net.firejack.platform.process.case.find-statuses',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.find-statuses';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.case.find-statuses.soap-example',
		'net.firejack.platform.process.case.find-statuses',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.find-statuses';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.case.find-statuses.blazeds-example',
		'net.firejack.platform.process.case.find-statuses',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.find-statuses';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.case.find-through-actors.rest-example',
		'net.firejack.platform.process.case.find-through-actors',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.find-through-actors';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.case.find-through-actors.soap-example',
		'net.firejack.platform.process.case.find-through-actors',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.find-through-actors';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.case.find-through-actors.blazeds-example',
		'net.firejack.platform.process.case.find-through-actors',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.find-through-actors';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.case.find-belonging-to-user-actor.rest-example',
		'net.firejack.platform.process.case.find-belonging-to-user-actor',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.find-belonging-to-user-actor';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.case.find-belonging-to-user-actor.soap-example',
		'net.firejack.platform.process.case.find-belonging-to-user-actor',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.find-belonging-to-user-actor';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.case.find-belonging-to-user-actor.blazeds-example',
		'net.firejack.platform.process.case.find-belonging-to-user-actor',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.find-belonging-to-user-actor';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.case.find-closed-cases-for-current-user.rest-example',
		'net.firejack.platform.process.case.find-closed-cases-for-current-user',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.find-closed-cases-for-current-user';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.case.find-closed-cases-for-current-user.soap-example',
		'net.firejack.platform.process.case.find-closed-cases-for-current-user',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.find-closed-cases-for-current-user';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.case.find-closed-cases-for-current-user.blazeds-example',
		'net.firejack.platform.process.case.find-closed-cases-for-current-user',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.find-closed-cases-for-current-user';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.case.find-active.rest-example',
		'net.firejack.platform.process.case.find-active',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.find-active';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.case.find-active.soap-example',
		'net.firejack.platform.process.case.find-active',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.find-active';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.case.find-active.blazeds-example',
		'net.firejack.platform.process.case.find-active',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.find-active';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.case.reset.rest-example',
		'net.firejack.platform.process.case.reset',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.reset';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.case.reset.soap-example',
		'net.firejack.platform.process.case.reset',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.reset';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.case.reset.blazeds-example',
		'net.firejack.platform.process.case.reset',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case.reset';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.task.read-next-assignee-candidates.rest-example',
		'net.firejack.platform.process.task.read-next-assignee-candidates',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.read-next-assignee-candidates';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.task.read-next-assignee-candidates.soap-example',
		'net.firejack.platform.process.task.read-next-assignee-candidates',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.read-next-assignee-candidates';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.task.read-next-assignee-candidates.blazeds-example',
		'net.firejack.platform.process.task.read-next-assignee-candidates',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.read-next-assignee-candidates';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.task.read-assignee-candidates.rest-example',
		'net.firejack.platform.process.task.read-assignee-candidates',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.read-assignee-candidates';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.task.read-assignee-candidates.soap-example',
		'net.firejack.platform.process.task.read-assignee-candidates',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.read-assignee-candidates';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.task.read-assignee-candidates.blazeds-example',
		'net.firejack.platform.process.task.read-assignee-candidates',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.read-assignee-candidates';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.task.read-previous-assignee-candidates.rest-example',
		'net.firejack.platform.process.task.read-previous-assignee-candidates',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.read-previous-assignee-candidates';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.task.read-previous-assignee-candidates.soap-example',
		'net.firejack.platform.process.task.read-previous-assignee-candidates',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.read-previous-assignee-candidates';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.task.read-previous-assignee-candidates.blazeds-example',
		'net.firejack.platform.process.task.read-previous-assignee-candidates',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.read-previous-assignee-candidates';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.task.search-through-actors.rest-example',
		'net.firejack.platform.process.task.search-through-actors',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.search-through-actors';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.task.search-through-actors.soap-example',
		'net.firejack.platform.process.task.search-through-actors',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.search-through-actors';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.task.search-through-actors.blazeds-example',
		'net.firejack.platform.process.task.search-through-actors',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.search-through-actors';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.task.filter-all.rest-example',
		'net.firejack.platform.process.task.filter-all',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.filter-all';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.task.filter-all.soap-example',
		'net.firejack.platform.process.task.filter-all',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.filter-all';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.task.filter-all.blazeds-example',
		'net.firejack.platform.process.task.filter-all',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.filter-all';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.task.find-closed-tasks-for-current-user.rest-example',
		'net.firejack.platform.process.task.find-closed-tasks-for-current-user',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.find-closed-tasks-for-current-user';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.task.find-closed-tasks-for-current-user.soap-example',
		'net.firejack.platform.process.task.find-closed-tasks-for-current-user',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.find-closed-tasks-for-current-user';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.task.find-closed-tasks-for-current-user.blazeds-example',
		'net.firejack.platform.process.task.find-closed-tasks-for-current-user',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.find-closed-tasks-for-current-user';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.task.find-belonging-to-user-actor.rest-example',
		'net.firejack.platform.process.task.find-belonging-to-user-actor',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.find-belonging-to-user-actor';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.task.find-belonging-to-user-actor.soap-example',
		'net.firejack.platform.process.task.find-belonging-to-user-actor',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.find-belonging-to-user-actor';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.task.find-belonging-to-user-actor.blazeds-example',
		'net.firejack.platform.process.task.find-belonging-to-user-actor',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.task.find-belonging-to-user-actor';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.case-object.search-by-assignee.rest-example',
		'net.firejack.platform.process.case-object.search-by-assignee',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case-object.search-by-assignee';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.case-object.search-by-assignee.soap-example',
		'net.firejack.platform.process.case-object.search-by-assignee',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case-object.search-by-assignee';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.case-object.search-by-assignee.blazeds-example',
		'net.firejack.platform.process.case-object.search-by-assignee',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case-object.search-by-assignee';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.case-note.read-by-task.rest-example',
		'net.firejack.platform.process.case-note.read-by-task',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case-note.read-by-task';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.case-note.read-by-task.soap-example',
		'net.firejack.platform.process.case-note.read-by-task',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case-note.read-by-task';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.case-note.read-by-task.blazeds-example',
		'net.firejack.platform.process.case-note.read-by-task',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case-note.read-by-task';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.case-note.read-by-case.rest-example',
		'net.firejack.platform.process.case-note.read-by-case',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case-note.read-by-case';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.case-note.read-by-case.soap-example',
		'net.firejack.platform.process.case-note.read-by-case',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case-note.read-by-case';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.case-note.read-by-case.blazeds-example',
		'net.firejack.platform.process.case-note.read-by-case',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case-note.read-by-case';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.case-note.read-by-case-object.rest-example',
		'net.firejack.platform.process.case-note.read-by-case-object',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case-note.read-by-case-object';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.case-note.read-by-case-object.soap-example',
		'net.firejack.platform.process.case-note.read-by-case-object',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case-note.read-by-case-object';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.case-note.read-by-case-object.blazeds-example',
		'net.firejack.platform.process.case-note.read-by-case-object',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case-note.read-by-case-object';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.case-note.search-by-task.rest-example',
		'net.firejack.platform.process.case-note.search-by-task',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case-note.search-by-task';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.case-note.search-by-task.soap-example',
		'net.firejack.platform.process.case-note.search-by-task',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case-note.search-by-task';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.case-note.search-by-task.blazeds-example',
		'net.firejack.platform.process.case-note.search-by-task',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case-note.search-by-task';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'net.firejack.platform.process.case-note.search-by-case.rest-example',
		'net.firejack.platform.process.case-note.search-by-case',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case-note.search-by-case';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'net.firejack.platform.process.case-note.search-by-case.soap-example',
		'net.firejack.platform.process.case-note.search-by-case',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case-note.search-by-case';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'net.firejack.platform.process.case-note.search-by-case.blazeds-example',
		'net.firejack.platform.process.case-note.search-by-case',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'net.firejack.platform.process.case-note.search-by-case';

-- advertiser
insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'com.millennialmedia.advertiser.client-vo.map.rest-example',
		'com.millennialmedia.advertiser.client-vo.map',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'com.millennialmedia.advertiser.client-vo.map';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'com.millennialmedia.advertiser.client-vo.map.soap-example',
		'com.millennialmedia.advertiser.client-vo.map',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'com.millennialmedia.advertiser.client-vo.map';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'com.millennialmedia.advertiser.client-vo.map.blazeds-example',
		'com.millennialmedia.advertiser.client-vo.map',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'com.millennialmedia.advertiser.client-vo.map';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'com.millennialmedia.advertiser.client-vo.unmap.rest-example',
		'com.millennialmedia.advertiser.client-vo.unmap',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'com.millennialmedia.advertiser.client-vo.unmap';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'com.millennialmedia.advertiser.client-vo.unmap.soap-example',
		'com.millennialmedia.advertiser.client-vo.unmap',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'com.millennialmedia.advertiser.client-vo.unmap';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'com.millennialmedia.advertiser.client-vo.unmap.blazeds-example',
		'com.millennialmedia.advertiser.client-vo.unmap',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'com.millennialmedia.advertiser.client-vo.unmap';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'com.millennialmedia.advertiser.invoice.invoice-line-association.read.rest-example',
		'com.millennialmedia.advertiser.invoice.invoice-line-association.read',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'com.millennialmedia.advertiser.invoice.invoice-line-association.read';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'com.millennialmedia.advertiser.invoice.invoice-line-association.read.soap-example',
		'com.millennialmedia.advertiser.invoice.invoice-line-association.read',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'com.millennialmedia.advertiser.invoice.invoice-line-association.read';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'com.millennialmedia.advertiser.invoice.invoice-line-association.read.blazeds-example',
		'com.millennialmedia.advertiser.invoice.invoice-line-association.read',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'com.millennialmedia.advertiser.invoice.invoice-line-association.read';

-- sales

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'REST Example',
		'com.millennialmedia.sales.sales-force-model-vo.read-all-related.rest-example',
		'com.millennialmedia.sales.sales-force-model-vo.read-all-related',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'com.millennialmedia.advertiser.client-vo.map';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'SOAP Example',
		'com.millennialmedia.sales.sales-force-model-vo.read-all-related.soap-example',
		'com.millennialmedia.sales.sales-force-model-vo.read-all-related',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'com.millennialmedia.sales.sales-force-model-vo.read-all-related';

insert into opf_registry_node (name, lookup, path, type, id_parent)
    select
	    'BlazeDS Example',
		'com.millennialmedia.sales.sales-force-model-vo.read-all-related.blazeds-example',
		'com.millennialmedia.sales.sales-force-model-vo.read-all-related',
		'TXT',
		action.id
	from opf_registry_node action
	where action.lookup = 'com.millennialmedia.sales.sales-force-model-vo.read-all-related';


-- ----------------------------  Resources Versions  ---------------------------------
-- Open Flame

-- advertiser


--  TODO


-- ----------------------------------   Tasks   -------------------------------------
-- advertiser
insert into opf_task (active, update_date, close_date, id_case, id_activity, id_assignee)
   select
      false,
	  NOW(),
	  NOW(),
	  nextTask.id_case,
	  activity.id,
	  admin.id
   from opf_activity activity, opf_user admin,
        opf_task nextTask
         inner join opf_activity nextActivity on nextActivity.id = nextTask.id_activity
   where activity.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.account-manager-ready' and
         admin.username = 'admin' and
		 nextActivity.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.delivery-manager-review';

insert into opf_task (active, update_date, close_date, id_case, id_activity, id_assignee)
   select
      false,
	  NOW(),
	  NOW(),
	  nextTask.id_case,
	  activity.id,
	  admin.id
   from opf_activity activity, opf_user admin,
        opf_task nextTask
         inner join opf_activity nextActivity on nextActivity.id = nextTask.id_activity
   where activity.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.delivery-manager-ready' and
         admin.username = 'admin' and
		 nextActivity.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.finance-review';

insert into opf_task (active, update_date, close_date, id_case, id_activity, id_assignee)
   select
      false,
	  NOW(),
	  NOW(),
	  nextTask.id_case,
	  activity.id,
	  admin.id
   from opf_activity activity, opf_user admin,
        opf_task nextTask
         inner join opf_activity nextActivity on nextActivity.id = nextTask.id_activity
   where activity.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.finance-ready' and
         admin.username = 'admin' and
		 nextActivity.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.system-export';

-- --------------------------------   Case Actions   -----------------------------------
-- advertiser

insert into opf_case_action (type, performed_on, id_case, id_task, id_user)
   select
       1,
       NOW(),
	   task.id_case,
	   task.id,
	   admin.id
   from opf_user admin, opf_task task
      inner join opf_activity task_acivity on task_acivity.id = task.id_activity
   where admin.username = 'admin' and
         task_acivity.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.account-manager-ready';

insert into opf_case_action (type, performed_on, id_case, id_task, id_user)
   select
       1,
       NOW(),
	   task.id_case,
	   task.id,
	   admin.id
   from opf_user admin, opf_task task
      inner join opf_activity task_acivity on task_acivity.id = task.id_activity
   where admin.username = 'admin' and
         task_acivity.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.delivery-manager-ready';

insert into opf_case_action (type, performed_on, id_case, id_task, id_user)
   select
       1,
       NOW(),
	   task.id_case,
	   task.id,
	   admin.id
   from opf_user admin, opf_task task
      inner join opf_activity task_acivity on task_acivity.id = task.id_activity
   where admin.username = 'admin' and
         task_acivity.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process.finance-ready';


-- --------------------------------   Configs   -----------------------------------
-- advertiser
insert into opf_config (name, description, lookup, path, value, id_parent)
   select
      'generate',
	  'Turn invoice generation: true or false',
	  'com.millennialmedia.advertiser.invoice.generate',
	  'com.millennialmedia.advertiser.invoice',
	  'false',
	  par.id
   from opf_registry_node par
   where par.lookup = 'com.millennialmedia.advertiser.invoice';

-- --------------------------------   Explanations   -----------------------------------
-- advertiser

insert into opf_case_explanation(short_description, long_description, id_process)
  select
    'Exceeded IO amount',
    'Exceeded IO amount',
    rn.id
  from opf_registry_node rn
  where rn.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process';

 insert into opf_case_explanation(short_description, long_description, id_process)
  select
    '3rd party billed',
    '3rd party billed',
    rn.id
  from opf_registry_node rn
  where rn.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process';

 insert into opf_case_explanation(short_description, long_description, id_process)
  select
    'Make good/bonus',
    'Make good/bonus',
    rn.id
  from opf_registry_node rn
  where rn.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process';

  insert into opf_case_explanation(short_description, long_description, id_process)
  select
    'Other',
    'Other',
    rn.id
  from opf_registry_node rn
  where rn.lookup = 'com.millennialmedia.advertiser.invoice.invoice-approval-process';
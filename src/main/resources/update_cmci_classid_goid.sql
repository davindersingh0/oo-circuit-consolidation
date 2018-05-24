-- Added function to transform CI class from old circuit class to new circuit class
-- Update CI with new classid and goid
--DROP FUNCTION cm_update_ci_classid_classname_goid(bigint,bigint,integer,character varying,character varying,character varying,integer,bigint,character varying);
CREATE OR REPLACE FUNCTION cm_update_ci_classid_classname_goid(p_ci_id bigint, p_ns_id bigint, p_class_id integer, p_goid character varying, p_ci_name character varying, p_comments character varying, p_state_id integer, p_last_rfc_id bigint, p_created_by character varying)
  RETURNS void 
  AS
$BODY$
DECLARE
    l_class_id integer;
    l_class_name character varying;
    l_ci_name character varying;
    l_comments character varying;
    l_state_id integer;
BEGIN
    select into l_class_id, l_class_name, l_ci_name, l_comments, l_state_id   
		 cl.class_id, cl.class_name, ci.ci_name, ci.comments, ci.ci_state_id  
    from cm_ci ci, md_classes cl
    where ci.ci_id = p_ci_id
      and ci.class_id = cl.class_id;

    update cm_ci 
     set class_id=p_class_id,
     	 ci_goid=p_goid,
         comments = coalesce(p_comments, comments),
         ci_state_id = coalesce(p_state_id, ci_state_id),
         last_applied_rfc_id = coalesce(p_last_rfc_id, last_applied_rfc_id),
         updated_by = p_created_by,
         updated = now()
    where ci_id = p_ci_id;

    insert into cms_ci_event_queue(event_id, source_pk, source_name, event_type_id)
    values (nextval('event_pk_seq'), p_ci_id, 'cm_ci' , 200);

    insert into cm_ci_log(log_id, log_time, log_event, ci_id, ci_name, class_id, class_name, comments, ci_state_id, ci_state_id_old, p_created_by)
    values (nextval('log_pk_seq'), now(), 200, p_ci_id, coalesce(p_ci_name, l_ci_name), l_class_id, l_class_name, coalesce(p_comments, l_comments), l_state_id, coalesce(p_state_id, l_state_id), p_created_by);
    
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
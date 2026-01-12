-- ТРИГГЕРЫ
-- при просмотре программы создавать запись в таблице для логов, что автоматом увеличит значение
CREATE OR REPLACE FUNCTION increment_program_view()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE art2art_program_stats
    SET views_count = views_count + 1,
        updated_at = now()
    WHERE program_id = NEW.program_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_program_view
AFTER INSERT ON art2art_program_views_log
FOR EACH ROW
EXECUTE FUNCTION increment_program_view();


-- при просмотре резиденции создавать запись в таблице для логов, что автоматом увеличит значение
CREATE OR REPLACE FUNCTION increment_residence_view()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE art2art_residence_stats
    SET views_count = views_count + 1,
        updated_at = now()
    WHERE residence_id = NEW.residence_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_residence_view
AFTER INSERT ON art2art_residence_views_log
FOR EACH ROW
EXECUTE FUNCTION increment_residence_view();


-- автоматическое увеличение количества заявок для программы
CREATE OR REPLACE FUNCTION increment_applications_count()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE art2art_program_stats
    SET applications_count = applications_count + 1,
        updated_at = now()
    WHERE program_id = NEW.program_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_program_application
AFTER INSERT ON art2art_application_requests
FOR EACH ROW
EXECUTE FUNCTION increment_applications_count();


-- автоматическая публикация резиденции при одобрении заявки на валидацию
CREATE OR REPLACE FUNCTION update_residence_publish_status()
RETURNS TRIGGER AS $$
BEGIN
    -- Обновляем резиденцию, устанавливая is_published = TRUE
    UPDATE art2art_residence_details
    SET is_published = TRUE,
        validation_submitted_at = now(),
        updated_at = now()
    WHERE id = NEW.residence_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validation_request_approved
AFTER UPDATE ON art2art_residence_details
FOR EACH ROW
WHEN (NEW.validation_status = 'approved' AND OLD.validation_status IS DISTINCT FROM 'approved')
EXECUTE FUNCTION update_residence_publish_status();
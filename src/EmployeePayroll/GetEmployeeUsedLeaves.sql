CREATE PROCEDURE GetEmployeeUsedLeaves
    @EmployeeId INT,
    @Year INT
AS
BEGIN
    SELECT ISNULL(SUM(DATEDIFF(DAY, lr.leave_start_date, lr.leave_end_date) + 1), 0) AS used_leaves
    FROM LeaveRequest lr
    JOIN LeaveStatus ls ON lr.status_id = ls.status_id
    WHERE lr.employee_id = @EmployeeId
    AND YEAR(lr.leave_start_date) = @Year
    AND ls.status = 'Approved'
END 
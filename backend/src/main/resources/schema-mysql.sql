-- 家庭智联服务系统 V1.0 数据库结构 (MySQL 8)
-- 主键统一使用 ASSIGN_ID (雪花算法)，不使用自增

CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(100) NOT NULL,
  enabled TINYINT DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS customer (
  id BIGINT PRIMARY KEY,
  customer_name VARCHAR(50),
  phone VARCHAR(30),
  community VARCHAR(100),
  source VARCHAR(30),
  status VARCHAR(30),
  next_contact_at DATETIME,
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS customer_member (
  id BIGINT PRIMARY KEY,
  customer_id BIGINT,
  member_name VARCHAR(50),
  relation VARCHAR(30),
  phone VARCHAR(30),
  operator VARCHAR(30),
  fee DECIMAL(10,2),
  monthly_data_gb DECIMAL(10,2),
  monthly_call_minutes INT,
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS customer_plan (
  id BIGINT PRIMARY KEY,
  customer_id BIGINT,
  member_id BIGINT,
  plan_name VARCHAR(100),
  operator VARCHAR(30),
  plan_type VARCHAR(30),
  monthly_fee DECIMAL(10,2),
  extra_monthly_fee DECIMAL(10,2),
  data_gb DECIMAL(10,2),
  call_minutes INT,
  broadband_speed INT,
  contract_end_date DATE,
  contract_months INT,
  equipment_fee DECIMAL(10,2),
  fee_accuracy VARCHAR(20),
  effective_status VARCHAR(20),
  remark VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS plan_product (
  id BIGINT PRIMARY KEY,
  plan_name VARCHAR(100),
  operator VARCHAR(30),
  plan_type VARCHAR(30),
  standard_fee DECIMAL(10,2),
  discount_fee DECIMAL(10,2),
  discount_months INT,
  after_discount_fee DECIMAL(10,2),
  data_gb DECIMAL(10,2),
  call_minutes INT,
  broadband_speed INT,
  sub_card_count INT,
  contract_months INT,
  installation_fee DECIMAL(10,2),
  equipment_fee DECIMAL(10,2),
  conditions VARCHAR(1000),
  description VARCHAR(1000),
  source_note VARCHAR(255),
  verified_at DATETIME,
  status TINYINT DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS plan_analysis (
  id BIGINT PRIMARY KEY,
  customer_id BIGINT,
  analysis_no VARCHAR(50),
  current_monthly_fee DECIMAL(10,2),
  recommended_monthly_fee DECIMAL(10,2),
  after_discount_monthly_fee DECIMAL(10,2),
  one_time_fee DECIMAL(10,2),
  comparison_months INT,
  current_total_fee DECIMAL(10,2),
  recommended_total_fee DECIMAL(10,2),
  estimated_saving DECIMAL(10,2),
  recommendation_reason VARCHAR(2000),
  customer_feedback VARCHAR(30),
  deal_status VARCHAR(30),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS plan_analysis_item (
  id BIGINT PRIMARY KEY,
  analysis_id BIGINT,
  product_id BIGINT,
  product_name VARCHAR(100),
  operator VARCHAR(30),
  plan_type VARCHAR(30),
  monthly_fee DECIMAL(10,2),
  discount_fee DECIMAL(10,2),
  discount_months INT,
  after_discount_fee DECIMAL(10,2),
  equipment_fee DECIMAL(10,2),
  installation_fee DECIMAL(10,2),
  contract_months INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS customer_service_record (
  id BIGINT PRIMARY KEY,
  customer_id BIGINT,
  service_type VARCHAR(30),
  service_time DATETIME,
  service_content VARCHAR(1000),
  service_result VARCHAR(1000),
  follow_up_date DATETIME,
  status VARCHAR(30),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS customer_consent (
  id BIGINT PRIMARY KEY,
  customer_id BIGINT,
  consent_type VARCHAR(50),
  consent_status TINYINT DEFAULT 1,
  consent_scope VARCHAR(500),
  consent_time DATETIME,
  revoked_at DATETIME,
  remark VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

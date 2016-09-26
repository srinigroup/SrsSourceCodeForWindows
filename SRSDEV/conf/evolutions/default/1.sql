# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table account_holder (
  id                        bigint auto_increment not null,
  account_holder            varchar(255),
  store_id                  bigint,
  address_id                bigint,
  contact_info_id           bigint,
  constraint pk_account_holder primary key (id))
;

create table address (
  id                        bigint auto_increment not null,
  street                    varchar(255),
  number                    varchar(255),
  postal_code               varchar(255),
  city                      varchar(255),
  state                     varchar(255),
  country                   varchar(255),
  constraint pk_address primary key (id))
;

create table bank_account_info (
  id                        bigint auto_increment not null,
  account_name              varchar(255),
  account_number            varchar(255),
  account_type              varchar(255),
  bank_name                 varchar(255),
  bsb_number                varchar(255),
  constraint pk_bank_account_info primary key (id))
;

create table bank_deposit (
  id                        bigint auto_increment not null,
  create_date               datetime,
  cash_amt                  double,
  cheque_amt                double,
  constraint pk_bank_deposit primary key (id))
;

create table company (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  director                  varchar(255),
  description               varchar(255),
  status                    varchar(255),
  address_id                bigint,
  contact_info_id           bigint,
  constraint pk_company primary key (id))
;

create table contact_info (
  id                        bigint auto_increment not null,
  phone1                    varchar(255),
  phone2                    varchar(255),
  email                     varchar(255),
  facsimile                 varchar(255),
  constraint pk_contact_info primary key (id))
;

create table daily_reconciliation (
  id                        bigint auto_increment not null,
  create_date               datetime,
  reporting_business_date   datetime,
  open_cash                 double,
  close_cash                double,
  open_cheque               double,
  close_cheque              double,
  status                    varchar(255),
  daily_report_file         varchar(255),
  dsr_id                    bigint,
  bank_deposit_id           bigint,
  store_id                  bigint,
  constraint pk_daily_reconciliation primary key (id))
;

create table daily_sales_reconciliation (
  id                        bigint auto_increment not null,
  today_date                datetime,
  reporting_business_date   datetime,
  store_id                  bigint,
  status                    varchar(255),
  cash_in_safe              double,
  cheques_in_safe           double,
  constraint pk_daily_sales_reconciliation primary key (id))
;

create table employee (
  id                        bigint auto_increment not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  short_name                varchar(255),
  gender                    varchar(255),
  dob                       datetime,
  emergency_contact_name    varchar(255),
  emergency_relation        varchar(255),
  emergency_phone           varchar(255),
  emergency_email           varchar(255),
  company_id                bigint,
  store_id                  bigint,
  ecd                       datetime,
  emp_status                varchar(255),
  sal                       varchar(255),
  designation               varchar(255),
  is_tax_free               varchar(255),
  tfndf                     varchar(255),
  saf                       varchar(255),
  resume                    varchar(255),
  payroll                   varchar(255),
  status                    varchar(255),
  address_id                bigint,
  contact_info_id           bigint,
  bank_account_info_id      bigint,
  constraint pk_employee primary key (id))
;

create table expense_head (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  category                  varchar(255),
  status                    varchar(255),
  created_date              datetime,
  modified_date             datetime,
  constraint pk_expense_head primary key (id))
;

create table head_office_time_sheet (
  id                        bigint auto_increment not null,
  date                      datetime,
  end_date                  datetime,
  duration                  varchar(255),
  start_time_hour           varchar(255),
  start_time_mins           varchar(255),
  end_time_hour             varchar(255),
  end_time_mins             varchar(255),
  leave_type                varchar(255),
  empid                     bigint,
  job_title                 varchar(255),
  activity                  varchar(255),
  constraint pk_head_office_time_sheet primary key (id))
;

create table ho_form (
  id                        bigint auto_increment not null,
  file_name                 varchar(255),
  form_type                 varchar(255),
  uploaded_date             datetime,
  constraint pk_ho_form primary key (id))
;

create table invoice (
  id                        bigint auto_increment not null,
  file_name                 varchar(255),
  invoice_type              varchar(255),
  file_path                 varchar(255),
  store_id                  bigint,
  company_id                bigint,
  supplier_id               bigint,
  uploaded_date             datetime,
  processed_date            datetime,
  invoice_date              datetime,
  payment_mode              varchar(255),
  status                    varchar(255),
  constraint pk_invoice primary key (id))
;

create table login1 (
  email                     varchar(255) not null,
  password                  varchar(255),
  constraint pk_login1 primary key (email))
;

create table media_tender (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  status                    varchar(255),
  category                  varchar(255),
  constraint pk_media_tender primary key (id))
;

create table misc_expense (
  id                        bigint auto_increment not null,
  expense_amt               double,
  expense_type              varchar(255),
  daily_reconciliation_id   bigint,
  constraint pk_misc_expense primary key (id))
;

create table monthly_report (
  id                        bigint auto_increment not null,
  file_name                 varchar(255),
  file_path                 varchar(255),
  uploaded_date             datetime,
  store_id                  bigint,
  reporting_month           varchar(255),
  report_type               varchar(255),
  constraint pk_monthly_report primary key (id))
;

create table payment_tender (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  category                  varchar(255),
  status                    varchar(255),
  constraint pk_payment_tender primary key (id))
;

create table payout (
  id                        bigint auto_increment not null,
  supplier_id               bigint,
  shift_id                  bigint,
  daily_reconciliation_id   bigint,
  invoice_amt               double,
  payout_type               varchar(255),
  category                  varchar(255),
  invoice                   varchar(255),
  reason                    varchar(255),
  constraint pk_payout primary key (id))
;

create table payroll (
  id                        bigint auto_increment not null,
  date                      datetime,
  from_date                 datetime,
  to_date                   datetime,
  daily_reconciliation_id   bigint,
  pay_amt                   double,
  empid                     bigint,
  constraint pk_payroll primary key (id))
;

create table product (
  id                        bigint auto_increment not null,
  ean                       bigint,
  name                      varchar(255),
  description               varchar(255),
  constraint pk_product primary key (id))
;

create table role (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  description               varchar(255),
  constraint pk_role primary key (id))
;

create table sales_head (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  category                  varchar(255),
  status                    varchar(255),
  created_date              datetime,
  modified_date             datetime,
  constraint pk_sales_head primary key (id))
;

create table shift (
  id                        bigint auto_increment not null,
  shift_start_date_time     datetime,
  shift_end_date_time       datetime,
  timesheet_id              bigint,
  status                    varchar(255),
  terminal_id               bigint,
  shift_variance            double,
  var_reason                varchar(255),
  constraint pk_shift primary key (id))
;

create table shift_media_collected (
  id                        bigint auto_increment not null,
  media_tender_id           bigint,
  amount                    double,
  settle_amount             double,
  shift_id                  bigint,
  constraint pk_shift_media_collected primary key (id))
;

create table shift_payment_tender (
  id                        bigint auto_increment not null,
  payment_tender_id         bigint,
  amount                    double,
  shift_id                  bigint,
  constraint pk_shift_payment_tender primary key (id))
;

create table shift_sale (
  id                        bigint auto_increment not null,
  sales_head_id             bigint,
  amount                    double,
  shift_id                  bigint,
  constraint pk_shift_sale primary key (id))
;

create table shift_variance (
  id                        bigint auto_increment not null,
  cash_amt                  double,
  eftpos_amt                double,
  cash_var_reason           varchar(255),
  eftpos_var_reason         varchar(255),
  constraint pk_shift_variance primary key (id))
;

create table store (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  description               varchar(255),
  status                    varchar(255),
  address_id                bigint,
  contact_info_id           bigint,
  company_id                bigint,
  cash_in_safe              double,
  cheques_in_safe           double,
  constraint pk_store primary key (id))
;

create table supplier (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  abn                       varchar(255),
  description               varchar(255),
  status                    varchar(255),
  address_id                bigint,
  contact_info_id           bigint,
  constraint pk_supplier primary key (id))
;

create table supplier_mapping (
  id                        bigint auto_increment not null,
  payment_terms             varchar(255),
  payment_mode              varchar(255),
  supplier_id               bigint,
  store_id                  bigint,
  constraint pk_supplier_mapping primary key (id))
;

create table terminal (
  id                        bigint auto_increment not null,
  terminal_head_id          bigint,
  gst_collected             double,
  daily_sales_reconciliation_id bigint,
  terminal_variance         double,
  var_reason                varchar(255),
  status                    varchar(255),
  constraint pk_terminal primary key (id))
;

create table terminal_head (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  category                  varchar(255),
  status                    varchar(255),
  constraint pk_terminal_head primary key (id))
;

create table timesheet (
  id                        bigint auto_increment not null,
  date                      datetime,
  end_date                  datetime,
  duration                  varchar(255),
  start_time_hour           varchar(255),
  start_time_mins           varchar(255),
  end_time_hour             varchar(255),
  end_time_mins             varchar(255),
  leave_type                varchar(255),
  empid                     bigint,
  firmid                    bigint,
  firm_type                 varchar(255),
  activity                  varchar(255),
  job_title                 varchar(255),
  status                    varchar(255),
  constraint pk_timesheet primary key (id))
;

create table total_sales_head (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  category                  varchar(255),
  created_date              datetime,
  modified_date             datetime,
  constraint pk_total_sales_head primary key (id))
;

create table total_settlement_sale (
  id                        bigint auto_increment not null,
  total_sales_head_id       bigint,
  amount                    double,
  terminal_id               bigint,
  constraint pk_total_settlement_sale primary key (id))
;

create table account (
  id                        bigint auto_increment not null,
  email                     varchar(255),
  name                      varchar(255),
  password                  varchar(255),
  constraint pk_account primary key (id))
;


create table company_employee (
  company_id                     bigint not null,
  employee_id                    bigint not null,
  constraint pk_company_employee primary key (company_id, employee_id))
;

create table store_employee (
  store_id                       bigint not null,
  employee_id                    bigint not null,
  constraint pk_store_employee primary key (store_id, employee_id))
;

create table store_sales_head (
  store_id                       bigint not null,
  sales_head_id                  bigint not null,
  constraint pk_store_sales_head primary key (store_id, sales_head_id))
;

create table store_media_tender (
  store_id                       bigint not null,
  media_tender_id                bigint not null,
  constraint pk_store_media_tender primary key (store_id, media_tender_id))
;

create table store_payment_tender (
  store_id                       bigint not null,
  payment_tender_id              bigint not null,
  constraint pk_store_payment_tender primary key (store_id, payment_tender_id))
;

create table store_expense_head (
  store_id                       bigint not null,
  expense_head_id                bigint not null,
  constraint pk_store_expense_head primary key (store_id, expense_head_id))
;

create table store_total_sales_head (
  store_id                       bigint not null,
  total_sales_head_id            bigint not null,
  constraint pk_store_total_sales_head primary key (store_id, total_sales_head_id))
;

create table store_terminal_head (
  store_id                       bigint not null,
  terminal_head_id               bigint not null,
  constraint pk_store_terminal_head primary key (store_id, terminal_head_id))
;

create table store_supplier (
  store_id                       bigint not null,
  supplier_id                    bigint not null,
  constraint pk_store_supplier primary key (store_id, supplier_id))
;

create table account_role (
  account_id                     bigint not null,
  role_id                        bigint not null,
  constraint pk_account_role primary key (account_id, role_id))
;
alter table account_holder add constraint fk_account_holder_store_1 foreign key (store_id) references store (id) on delete restrict on update restrict;
create index ix_account_holder_store_1 on account_holder (store_id);
alter table account_holder add constraint fk_account_holder_address_2 foreign key (address_id) references address (id) on delete restrict on update restrict;
create index ix_account_holder_address_2 on account_holder (address_id);
alter table account_holder add constraint fk_account_holder_contactInfo_3 foreign key (contact_info_id) references contact_info (id) on delete restrict on update restrict;
create index ix_account_holder_contactInfo_3 on account_holder (contact_info_id);
alter table company add constraint fk_company_address_4 foreign key (address_id) references address (id) on delete restrict on update restrict;
create index ix_company_address_4 on company (address_id);
alter table company add constraint fk_company_contactInfo_5 foreign key (contact_info_id) references contact_info (id) on delete restrict on update restrict;
create index ix_company_contactInfo_5 on company (contact_info_id);
alter table daily_reconciliation add constraint fk_daily_reconciliation_dsr_6 foreign key (dsr_id) references daily_sales_reconciliation (id) on delete restrict on update restrict;
create index ix_daily_reconciliation_dsr_6 on daily_reconciliation (dsr_id);
alter table daily_reconciliation add constraint fk_daily_reconciliation_bankDeposit_7 foreign key (bank_deposit_id) references bank_deposit (id) on delete restrict on update restrict;
create index ix_daily_reconciliation_bankDeposit_7 on daily_reconciliation (bank_deposit_id);
alter table daily_reconciliation add constraint fk_daily_reconciliation_store_8 foreign key (store_id) references store (id) on delete restrict on update restrict;
create index ix_daily_reconciliation_store_8 on daily_reconciliation (store_id);
alter table daily_sales_reconciliation add constraint fk_daily_sales_reconciliation_store_9 foreign key (store_id) references store (id) on delete restrict on update restrict;
create index ix_daily_sales_reconciliation_store_9 on daily_sales_reconciliation (store_id);
alter table employee add constraint fk_employee_company_10 foreign key (company_id) references company (id) on delete restrict on update restrict;
create index ix_employee_company_10 on employee (company_id);
alter table employee add constraint fk_employee_store_11 foreign key (store_id) references store (id) on delete restrict on update restrict;
create index ix_employee_store_11 on employee (store_id);
alter table employee add constraint fk_employee_address_12 foreign key (address_id) references address (id) on delete restrict on update restrict;
create index ix_employee_address_12 on employee (address_id);
alter table employee add constraint fk_employee_contactInfo_13 foreign key (contact_info_id) references contact_info (id) on delete restrict on update restrict;
create index ix_employee_contactInfo_13 on employee (contact_info_id);
alter table employee add constraint fk_employee_bankAccountInfo_14 foreign key (bank_account_info_id) references bank_account_info (id) on delete restrict on update restrict;
create index ix_employee_bankAccountInfo_14 on employee (bank_account_info_id);
alter table misc_expense add constraint fk_misc_expense_dailyReconciliation_15 foreign key (daily_reconciliation_id) references daily_reconciliation (id) on delete restrict on update restrict;
create index ix_misc_expense_dailyReconciliation_15 on misc_expense (daily_reconciliation_id);
alter table payout add constraint fk_payout_supplier_16 foreign key (supplier_id) references supplier (id) on delete restrict on update restrict;
create index ix_payout_supplier_16 on payout (supplier_id);
alter table payout add constraint fk_payout_shift_17 foreign key (shift_id) references shift (id) on delete restrict on update restrict;
create index ix_payout_shift_17 on payout (shift_id);
alter table payout add constraint fk_payout_dailyReconciliation_18 foreign key (daily_reconciliation_id) references daily_reconciliation (id) on delete restrict on update restrict;
create index ix_payout_dailyReconciliation_18 on payout (daily_reconciliation_id);
alter table payroll add constraint fk_payroll_dailyReconciliation_19 foreign key (daily_reconciliation_id) references daily_reconciliation (id) on delete restrict on update restrict;
create index ix_payroll_dailyReconciliation_19 on payroll (daily_reconciliation_id);
alter table shift add constraint fk_shift_timesheet_20 foreign key (timesheet_id) references timesheet (id) on delete restrict on update restrict;
create index ix_shift_timesheet_20 on shift (timesheet_id);
alter table shift add constraint fk_shift_terminal_21 foreign key (terminal_id) references terminal (id) on delete restrict on update restrict;
create index ix_shift_terminal_21 on shift (terminal_id);
alter table shift_media_collected add constraint fk_shift_media_collected_mediaTender_22 foreign key (media_tender_id) references media_tender (id) on delete restrict on update restrict;
create index ix_shift_media_collected_mediaTender_22 on shift_media_collected (media_tender_id);
alter table shift_media_collected add constraint fk_shift_media_collected_shift_23 foreign key (shift_id) references shift (id) on delete restrict on update restrict;
create index ix_shift_media_collected_shift_23 on shift_media_collected (shift_id);
alter table shift_payment_tender add constraint fk_shift_payment_tender_paymentTender_24 foreign key (payment_tender_id) references payment_tender (id) on delete restrict on update restrict;
create index ix_shift_payment_tender_paymentTender_24 on shift_payment_tender (payment_tender_id);
alter table shift_payment_tender add constraint fk_shift_payment_tender_shift_25 foreign key (shift_id) references shift (id) on delete restrict on update restrict;
create index ix_shift_payment_tender_shift_25 on shift_payment_tender (shift_id);
alter table shift_sale add constraint fk_shift_sale_salesHead_26 foreign key (sales_head_id) references sales_head (id) on delete restrict on update restrict;
create index ix_shift_sale_salesHead_26 on shift_sale (sales_head_id);
alter table shift_sale add constraint fk_shift_sale_shift_27 foreign key (shift_id) references shift (id) on delete restrict on update restrict;
create index ix_shift_sale_shift_27 on shift_sale (shift_id);
alter table store add constraint fk_store_address_28 foreign key (address_id) references address (id) on delete restrict on update restrict;
create index ix_store_address_28 on store (address_id);
alter table store add constraint fk_store_contactInfo_29 foreign key (contact_info_id) references contact_info (id) on delete restrict on update restrict;
create index ix_store_contactInfo_29 on store (contact_info_id);
alter table store add constraint fk_store_company_30 foreign key (company_id) references company (id) on delete restrict on update restrict;
create index ix_store_company_30 on store (company_id);
alter table supplier add constraint fk_supplier_address_31 foreign key (address_id) references address (id) on delete restrict on update restrict;
create index ix_supplier_address_31 on supplier (address_id);
alter table supplier add constraint fk_supplier_contactInfo_32 foreign key (contact_info_id) references contact_info (id) on delete restrict on update restrict;
create index ix_supplier_contactInfo_32 on supplier (contact_info_id);
alter table supplier_mapping add constraint fk_supplier_mapping_supplier_33 foreign key (supplier_id) references supplier (id) on delete restrict on update restrict;
create index ix_supplier_mapping_supplier_33 on supplier_mapping (supplier_id);
alter table supplier_mapping add constraint fk_supplier_mapping_store_34 foreign key (store_id) references store (id) on delete restrict on update restrict;
create index ix_supplier_mapping_store_34 on supplier_mapping (store_id);
alter table terminal add constraint fk_terminal_terminalHead_35 foreign key (terminal_head_id) references terminal_head (id) on delete restrict on update restrict;
create index ix_terminal_terminalHead_35 on terminal (terminal_head_id);
alter table terminal add constraint fk_terminal_dailySalesReconciliation_36 foreign key (daily_sales_reconciliation_id) references daily_sales_reconciliation (id) on delete restrict on update restrict;
create index ix_terminal_dailySalesReconciliation_36 on terminal (daily_sales_reconciliation_id);
alter table total_settlement_sale add constraint fk_total_settlement_sale_totalSalesHead_37 foreign key (total_sales_head_id) references total_sales_head (id) on delete restrict on update restrict;
create index ix_total_settlement_sale_totalSalesHead_37 on total_settlement_sale (total_sales_head_id);
alter table total_settlement_sale add constraint fk_total_settlement_sale_terminal_38 foreign key (terminal_id) references terminal (id) on delete restrict on update restrict;
create index ix_total_settlement_sale_terminal_38 on total_settlement_sale (terminal_id);



alter table company_employee add constraint fk_company_employee_company_01 foreign key (company_id) references company (id) on delete restrict on update restrict;

alter table company_employee add constraint fk_company_employee_employee_02 foreign key (employee_id) references employee (id) on delete restrict on update restrict;

alter table store_employee add constraint fk_store_employee_store_01 foreign key (store_id) references store (id) on delete restrict on update restrict;

alter table store_employee add constraint fk_store_employee_employee_02 foreign key (employee_id) references employee (id) on delete restrict on update restrict;

alter table store_sales_head add constraint fk_store_sales_head_store_01 foreign key (store_id) references store (id) on delete restrict on update restrict;

alter table store_sales_head add constraint fk_store_sales_head_sales_head_02 foreign key (sales_head_id) references sales_head (id) on delete restrict on update restrict;

alter table store_media_tender add constraint fk_store_media_tender_store_01 foreign key (store_id) references store (id) on delete restrict on update restrict;

alter table store_media_tender add constraint fk_store_media_tender_media_tender_02 foreign key (media_tender_id) references media_tender (id) on delete restrict on update restrict;

alter table store_payment_tender add constraint fk_store_payment_tender_store_01 foreign key (store_id) references store (id) on delete restrict on update restrict;

alter table store_payment_tender add constraint fk_store_payment_tender_payment_tender_02 foreign key (payment_tender_id) references payment_tender (id) on delete restrict on update restrict;

alter table store_expense_head add constraint fk_store_expense_head_store_01 foreign key (store_id) references store (id) on delete restrict on update restrict;

alter table store_expense_head add constraint fk_store_expense_head_expense_head_02 foreign key (expense_head_id) references expense_head (id) on delete restrict on update restrict;

alter table store_total_sales_head add constraint fk_store_total_sales_head_store_01 foreign key (store_id) references store (id) on delete restrict on update restrict;

alter table store_total_sales_head add constraint fk_store_total_sales_head_total_sales_head_02 foreign key (total_sales_head_id) references total_sales_head (id) on delete restrict on update restrict;

alter table store_terminal_head add constraint fk_store_terminal_head_store_01 foreign key (store_id) references store (id) on delete restrict on update restrict;

alter table store_terminal_head add constraint fk_store_terminal_head_terminal_head_02 foreign key (terminal_head_id) references terminal_head (id) on delete restrict on update restrict;

alter table store_supplier add constraint fk_store_supplier_store_01 foreign key (store_id) references store (id) on delete restrict on update restrict;

alter table store_supplier add constraint fk_store_supplier_supplier_02 foreign key (supplier_id) references supplier (id) on delete restrict on update restrict;

alter table account_role add constraint fk_account_role_account_01 foreign key (account_id) references account (id) on delete restrict on update restrict;

alter table account_role add constraint fk_account_role_role_02 foreign key (role_id) references role (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table account_holder;

drop table address;

drop table bank_account_info;

drop table bank_deposit;

drop table company;

drop table company_employee;

drop table contact_info;

drop table daily_reconciliation;

drop table daily_sales_reconciliation;

drop table employee;

drop table expense_head;

drop table head_office_time_sheet;

drop table ho_form;

drop table invoice;

drop table login1;

drop table media_tender;

drop table misc_expense;

drop table monthly_report;

drop table payment_tender;

drop table payout;

drop table payroll;

drop table product;

drop table role;

drop table sales_head;

drop table shift;

drop table shift_media_collected;

drop table shift_payment_tender;

drop table shift_sale;

drop table shift_variance;

drop table store;

drop table store_employee;

drop table store_sales_head;

drop table store_media_tender;

drop table store_payment_tender;

drop table store_expense_head;

drop table store_total_sales_head;

drop table store_terminal_head;

drop table store_supplier;

drop table supplier;

drop table supplier_mapping;

drop table terminal;

drop table terminal_head;

drop table timesheet;

drop table total_sales_head;

drop table total_settlement_sale;

drop table account;

drop table account_role;

SET FOREIGN_KEY_CHECKS=1;


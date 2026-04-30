--tells SQLite to turn on foreign keys
pragma foreign_keys = on;

create table if not exists profiles (
    profile_id integer primary key autoincrement,
    name text not null,
    pin text not null,
    max_daily_study_minutes integer not null check (max_daily_study_minutes >= 0)
);

create table if not exists profile_availability (
    profile_id integer not null,
    day_of_week text not null,
    minutes_available integer not null check (minutes_available >= 0),
    primary key (profile_id, day_of_week),
    foreign key (profile_id) references profiles(profile_id)
    on delete cascade
);

create table if not exists courses (
    course_id integer primary key autoincrement,
    profile_id integer not null,
    course_name text not null,
    course_difficulty integer not null check (course_difficulty between 1 and 5),
    foreign key (profile_id) references profiles(profile_id)
    on delete cascade
);

create table if not exists assessments (
    assessment_id integer primary key autoincrement,
    course_id integer not null,
    name text not null,
    type text not null,
    due_date text not null,
    estimated_minutes integer not null check (estimated_minutes > 0),
    weight integer not null check (weight between 1 and 100),
    difficulty integer not null check (difficulty between 1 and 5),
    foreign key (course_id) references courses(course_id)
    on delete cascade
);

create table if not exists week_plans (
    week_plan_id integer primary key autoincrement,
    profile_id integer not null,
    week_start_date text not null,
    --unique to make sure one profile doesn't have different plans for the same week (against using primary key which would make modelling relationship for study block easier)
    unique (profile_id, week_start_date),
    foreign key (profile_id) references profiles(profile_id)
    on delete cascade
);

create table if not exists study_blocks (
    study_block_id integer primary key autoincrement,
    week_plan_id integer not null,
    assessment_id integer not null,
    start_date_time text not null,
    end_date_time text not null,
    --Incomplete = 0 | Complete = 1
    complete integer not null default 0 check (complete in (0, 1)),
    foreign key (week_plan_id) references week_plans(week_plan_id)
    on delete cascade,
    foreign key (assessment_id) references assessments(assessment_id)
    on delete cascade
);
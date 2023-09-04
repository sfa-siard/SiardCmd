do $$
begin
for counter in 1..12
            loop
                insert into testdata(data) select data from testdata;
end loop;
end;
$$
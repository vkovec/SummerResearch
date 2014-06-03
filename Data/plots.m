function y = plots()
%plots the graphs for the 4 different states and 2 environments

%first load the data from the files (one file per state)
%each column represents a different action

%crossroads center
%cc = importdata('cc.txt', ',');

%crossroads start
%cs = importdata('cs.txt', ',');

%grid start area center
%gc = importdata('gc.txt', ',');

%grid start
%gs = importdata('gs.txt', ',');

gr = importdata('gr.txt', ',');

x = 0:1:999

%length(cc(:,1))

%for crossroads

%up, down, left, right, odown, oup

%{
figure;
plot(x, cs(:,1))
title('Crossroads (State 2): Up')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, cs(:,2))
title('Crossroads (State 2): Down')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, cs(:,3))
title('Crossroads (State 2): Left')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, cs(:,4))
title('Crossroads (State 2): Right')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, cs(:,5))
title('Crossroads (State 2): oDown')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, cs(:,6))
title('Crossroads (State 2): oUp')
xlabel('Trial')
ylabel('Probability')
grid on

%combined graph for all actions
figure;
plot(cs)
legend('Up', 'Down', 'Left', 'Right', 'oDown', 'oUp')
title('Crossroads (State 2)')
xlabel('Trial')
ylabel('Probability')
grid on
%}

%for grid

figure;
plot(x, gr(:,1))
title('Grid (State 15): Up')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, gr(:,2))
title('Grid (State 15): Down')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, gr(:,3))
title('Grid (State 15): Left')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, gr(:,4))
title('Grid (State 15): Right')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, gr(:,5))
title('Grid (State 15): oDown')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, gr(:,6))
title('Grid (State 15): oRight')
xlabel('Trial')
ylabel('Probability')
grid on

%combined graph for all actions
figure;
plot(gr)
legend('Up', 'Down', 'Left', 'Right', 'oDown', 'oRight')
title('Grid (State 15)')
xlabel('Trial')
ylabel('Probability')
grid on

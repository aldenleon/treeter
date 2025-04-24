Treeter is a tree-like Twitter (or X). Comments are always forming a tree, that's how they are presented and the way
you can navigate them. Comments are related to one another, forming a graph that can be used to better rank them. This
graph is stored in a relational database (postgresql) using foreign key constraints to point to parents and children.
The tree structure is formed directly in the sql query, achieving excellent performance.

The purpose is to build a free-form, open forum. There is no structure other than the tree and whatever the users
create. Any comment can be the root for a discussion-topic, for example. Comments allow basic (safe) html, so you can
even build a tree inside your comment if you get creative!

Ranking comments is central to Treeter. If users want to have a predefined order, they can always use parent-child
relationships. If users create siblings, it's up to Treeter to establish some order. I'm using Reddit's sorting
algorithm (See: https://www.evanmiller.org/how-not-to-sort-by-average-rating.html) embedded directly in the sql query
for optimal performance.

For local testing:
- Start postgresql server and create a database named 'treeterdb'
- Configure application-local.properties
- Run TreeterApplication with cli argument: --spring.profiles.active=local

For docker deployment:
- mvn package
- docker compose build
- docker compose create
- docker compose start|stop

Connect to http://localhost:3000/list-tree-view

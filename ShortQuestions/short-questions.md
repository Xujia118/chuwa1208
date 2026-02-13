**Explain and compare following concepts, provide specific examples when doing comparison**

### Testing Concepts

1. Unit Testing

- Definition: Testing the smallest units of code (e.g., functions, methods) in isolation.

- Goal: Ensure each unit works correctly on its own.

- Example: Testing a function add(a, b) to ensure it returns a + b.

- Comparison: Unlike integration testing, it does not check interactions between units.

2. Functional Testing

- Definition: Testing that the software behaves according to functional requirements. Focuses on “what the system does,” not “how it does it.”

- Example: Testing a login feature by entering valid credentials and checking if the user is logged in.

- Comparison: Functional testing is broader than unit testing; it can involve multiple units but still focuses on features, not internal implementation.

3. Integration Testing

- Definition: Testing how different modules or services work together.

- Example: After unit testing a payment module and an order module, test that placing an order triggers payment correctly.

- Comparison: Focuses on interactions between components, whereas unit testing focuses on isolated units.

4. Regression Testing

- Definition: Re-running previous tests to ensure new changes haven’t broken existing functionality.

- Example: After adding a discount feature, re-test the checkout process to ensure previous payment functionality still works.

- Comparison: Regression testing is not about new features; it’s about stability over time.

5. Smoke Testing

- Definition: A quick check to see if the basic functionality of the application works after a new build.

- Example: After deploying a new build, verify that the app launches, login works, and the main menu is accessible.

- Comparison: Smoke testing is shallow but wide, unlike unit or functional testing, which is deeper.

6. Performance Testing

- Definition: Testing how the system performs under expected workloads. Focuses on speed, responsiveness, and stability.

- Example: Measuring response time of a website when 1,000 users log in simultaneously.

- Comparison: Performance testing is about quality under normal load, while stress testing is about extreme load.

7. Stress Testing

- Definition: Testing how the system behaves under extreme conditions (beyond normal capacity).

- Example: Increasing concurrent user logins to 10,000 on a system designed for 1,000, to see if it crashes.

- Comparison: Stress testing is a type of performance testing but focuses on breaking points rather than expected behavior.

8. A/B Testing

- Definition: Comparing two versions (A and B) to see which performs better with real users.

- Example: Showing two different checkout page designs to 50% of users each and measuring conversion rate.

- Comparison: Unlike other testing types, A/B testing is experimental and user-focused, not code-focused.

9. End-to-End (E2E) Testing

- Definition: Testing the entire system flow, from start to finish, as a user would experience it.

- Example: Test placing an order from login → select product → payment → email confirmation.

- Comparison: E2E is broader than functional or integration testing; it validates the entire workflow, not just parts.

10. User Acceptance Testing (UAT)

- Definition: Testing by the end users or stakeholders to verify the system meets their requirements.

- Example: Business users test the reporting module to ensure it produces correct sales reports.

- Comparison: UAT is the final approval from users, whereas other tests are done by QA or developers.

### Environment Concepts

1. Development Environment

- Definition: Where developers write and test their code.

- Purpose: Fast feedback, code changes, experimentation.

- Example: Local machine with IDE, debugging tools, and mock services.

- Comparison: Code here may be unstable; not suitable for full testing by QA.

2. QA (Quality Assurance) Environment

- Definition: Environment for QA team to execute testing (functional, integration, regression).

- Purpose: Identify bugs before going to production.

- Example: QA server with realistic datasets but separate from production.

- Comparison: More controlled than dev; not exposed to real users.

3. Pre-prod / Staging Environment

- Definition: Replica of production used for final testing before release.

- Purpose: Verify deployment scripts, perform UAT, and test in production-like conditions.

- Example: Staging environment connected to production-like database, load balancers, and external APIs.

- Comparison: Higher fidelity than QA; changes are almost final.

4. Production Environment

- Definition: Live system accessed by real users.

- Purpose: Deliver the product/service to users.

- Example: Website or mobile app with real user traffic and data.

- Comparison: Cannot tolerate major bugs; all prior testing aims to prevent issues here.
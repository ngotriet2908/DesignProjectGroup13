import {Ability, AbilityBuilder } from "@casl/ability";
import store from "../../redux/store";

// Defines how to detect object's type
function subjectName(subject) {
  if (!subject || typeof subject === "string") return subject;
  return subject.__type
}

const ability = new Ability([], { subjectName })

let currentUser;
store.subscribe(() => {
  const prevUser = currentUser;
  //TODO not sure how to store this yet, roles are different depending on course
  currentUser = store.getState().user;
  if (prevUser !== currentUser) ability.update(defineRules(currentUser));
  console.log(currentUser.role);
});

function defineRules(user) {
  const { can, rules } = new AbilityBuilder(Ability);
  if (user.role === "teacher") {
    can("view", "Grade");
    can("add", "Grade");
    can("modify", "Grade");
    can("delete", "Grade");

    can("view", "Task");
    can("add", "Task");
    can("modify", "Task");
    can("delete", "Task");

    can("view", "GradingAssignment");
    can("add", "GradingAssignment");
    can("modify", "GradingAssignment");
    can("delete", "GradingAssignment");

    can("view", "Rubric");
    can("add", "Rubric");
    can("modify", "Rubric");
    can("delete", "Rubric");

    can("view", "Member"); //member of course
    can("view", "Group");

    can("view", "Submission");

    can("view", "Permission");
    can("add", "Permission");
    can("modify", "Permission");
    can("delete", "Permission");

    can("view", "Feedback");
    can("add", "Feedback");
    can("modify", "Feedback");
    can("delete", "Feedback");
  } else if (user.role === "ta") {
    can("view", "Grade");
    can("add", "Grade");
    can("modify", "Grade");
    can("delete", "Grade")

    can("view", "Rubric");

    can("view", "Task");

    can("view", "Member");
    can("view", "Group");

    can("view", "GradingAssignment");

    can("view", "Submission");

    can("view", "Permission", { authorId: user.user.id });

    can("view", "Feedback");
    can("add", "Feedback");
    can("modify", "Feedback", { authorId: user.user.id });
    can("delete", "Feedback", { authorId: user.user.id });
  }

  return rules;
}

export default ability;
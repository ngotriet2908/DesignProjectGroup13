export const rubric = {
  // content: {
  id: "1",
  projectId: "234234",
  // },
  children: [
    {
      content: {
        id: "2",
        type: "1",
        title: "criterionA",
        text: "<p>Hi, I'm you mama</p>",
        grade: {
          min: 0,
          max: 10,
          step: 1,
        }
      }
    },
    {
      content: {
        id: "3",
        type: "1",
        title: "criterionB",
        text: "<p>Hi, I'm you tata</p>",
        grade: {
          min: 0,
          max: 10,
          step: 1,
        }
      }
    },
    {
      content: {
        id: "4",
        type: "0",
        title: "blockA",
      },
      children: [
        {
          content: {
            id: "5",
            type: "0",
            title: "blockD",
          },
          children: [
            {
              content: {
                id: "6",
                type: "1",
                title: "criterion6",
                text: "<p>Hi, I'm you baba</p>",
                grade: {
                  min: 0,
                  max: 10,
                  step: 1,
                }
              }
            },
            {
              content: {
                id: "7",
                type: "1",
                title: "criterion7",
                text: "<p>Hi, I'm you dida</p>",
                grade: {
                  min: 0,
                  max: 10,
                  step: 1,
                }
              }
            }
          ]
        },
        {
          content: {
            id: "8",
            type: "1",
            title: "criterion8",
            text: "<p>Hi, I'm you teta</p>",
            grade: {
              min: 0,
              max: 10,
              step: 1,
            }
          }
        }
      ]
    },
    {
      content: {
        id: "9",
        type: "0",
        title: "blockB"
      },
      children: []
    },
    {
      content: {
        id: "10",
        type: "0",
        title: "blockC"
      },
      children: []
    }
  ]
}